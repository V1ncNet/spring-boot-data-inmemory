package de.team7.swt.core.persitence;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static de.team7.swt.core.persitence.IdUtils.getAccessorDeep;
import static de.team7.swt.core.persitence.IdUtils.getId;
import static de.team7.swt.core.persitence.IdUtils.setId;

/**
 * An in-memory implementation of Spring's {@literal CrudRepository} which stores entities in a {@literal HashMap}.
 *
 * @param <T>  the type of the entity to handle
 * @param <ID> the type of the entity's identifier
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class InMemoryRepository<T, ID> implements ListSupportingCrudRepository<T, ID> {

    public static final String ENTITY_MUST_NOT_BE_NULL = "Entity must not be null";
    public static final String ENTITIES_MUST_NOT_BE_NULL = "Entities must not be null";
    public static final String ID_MUST_NOT_BE_NULL = "ID must not be null";

    protected final Map<ID, T> store = new HashMap<>();

    @NonNull
    private final UnaryOperator<ID> nextId;

    private ID previousId;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);
        ID id = (ID) getId(entity);
        if (null == id) {
            id = nextId.apply(previousId);
            setId(entity, id);
        }

        store.put(id, entity);
        previousId = id;
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        Assert.notNull(entities, ENTITIES_MUST_NOT_BE_NULL);
        return stream(entities)
            .map(this::save)
            .collect(Collectors.toList());
    }

    private static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findById(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return Optional.ofNullable(store.get(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return store.containsKey(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        Assert.notNull(ids, "IDs must not be null");
        return stream(ids)
            .map(findOrThrow(id -> new NoResultException(String.format("No entity w/ id [%s] exists", id))))
            .collect(Collectors.toList());
    }

    private Function<ID, T> findOrThrow(Function<ID, RuntimeException> exception) {
        return id -> findById(id).orElseThrow(() -> exception.apply(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return store.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        findById(id).ifPresentOrElse(this::delete, () -> {
            throw new NoResultException(String.format("No entity w/ id [%s] exists", id));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void delete(T entity) {
        Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);

        ID id = (ID) getId(entity);
        if (null == id) {
            return;
        }

        Optional<T> existing = findById(id);
        if (existing.isEmpty()) {
            return;
        }

        store.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        Assert.notNull(entities, ENTITIES_MUST_NOT_BE_NULL);
        stream(entities).forEach(this::delete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        store.clear();
    }

    /**
     * Returns all instances of the type {@code T} with the given property and its value.
     *
     * @param propertyName must not be {@literal null}
     * @param value        might be {@literal null}
     * @param <F>          the type of the property's value
     * @return a subset of all entities matching the provided property name and its value
     */
    protected <F> Stream<T> findAllBy(String propertyName, F value) {
        Assert.notNull(propertyName, "Property name must not be null");
        return store.values().stream()
            .filter(by(propertyName, value));
    }

    private <F> Predicate<T> by(String property, F value) {
        return entity -> {
            try {
                return has(property, value, entity);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(
                    String.format(
                        "Couldn't get property [%s] from [%s]",
                        property,
                        entity.getClass().getCanonicalName()
                    ), e
                );
            }
        };
    }

    private <F> boolean has(String property, F value, T entity) throws NoSuchFieldException, IllegalAccessException {
        Field field = getAccessorDeep(entity, Class::getDeclaredFields, contains(property));
        if (null == field) {
            throw new NoSuchFieldException(
                String.format("Entity [%s] has no property [%s]", entity.getClass().getCanonicalName(), property)
            );
        }

        return Objects.equals(value, field.get(entity));
    }

    private static Predicate<Field> contains(String property) {
        return field -> Objects.equals(field.getName(), property);
    }

    /**
     * Returns a sole entity instance of the type {@code T} with the given property name and its value.
     *
     * @param propertyName must not be {@literal null}
     * @param value        might be {@literal null}
     * @param <F>          the type of the property's value
     * @return at most one entity matching the provided property name and its value
     * @throws NonUniqueResultException in case the repository contains more than one entity which matches the given
     *                                  query
     */
    protected <F> Optional<T> findUniqueBy(String propertyName, F value) throws NonUniqueResultException {
        List<T> resultList = findAllBy(propertyName, value).collect(Collectors.toList());

        switch (resultList.size()) {
            case 0:
                return Optional.empty();
            case 1:
                T unique = resultList.get(0);
                return Optional.ofNullable(unique);
            default:
                throw new NonUniqueResultException(String.format(
                    "Property [%s] is not a unique field in entity [%s]",
                    propertyName,
                    resultList.get(0).getClass().getCanonicalName()
                ));
        }
    }
}