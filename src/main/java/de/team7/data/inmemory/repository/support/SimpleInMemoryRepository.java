package de.team7.data.inmemory.repository.support;

import de.team7.data.domain.PrimaryKeyGenerator;
import de.team7.data.inmemory.repository.InMemoryRepository;
import de.team7.data.repository.NoResultException;
import de.team7.data.repository.NonUniqueResultException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.team7.data.inmemory.repository.support.IdUtils.getAccessorDeep;
import static de.team7.data.inmemory.repository.support.IdUtils.getId;
import static de.team7.data.inmemory.repository.support.IdUtils.setId;

/**
 * An in-memory implementation of Spring's {@literal CrudRepository} which stores entities in a {@literal HashMap}.
 *
 * @param <T>  the type of the entity to handle
 * @param <ID> the type of the entity's identifier
 * @author Vincent Nadoll
 */
@Repository
@RequiredArgsConstructor
public class SimpleInMemoryRepository<T, ID> implements InMemoryRepository<T, ID> {

    public static final String ENTITY_MUST_NOT_BE_NULL = "Entity must not be null";
    public static final String ENTITIES_MUST_NOT_BE_NULL = "Entities must not be null";
    public static final String ID_MUST_NOT_BE_NULL = "ID must not be null";

    protected final Map<ID, T> store = new HashMap<>();

    @NonNull
    private final PrimaryKeyGenerator<ID> primaryKeyGenerator;

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
            id = primaryKeyGenerator.next(previousId);
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
        return Streamable.of(entities)
            .map(this::save)
            .toList();
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
    public Streamable<T> findAll() {
        return Streamable.of(store.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Streamable<T> findAllById(Iterable<ID> ids) {
        Assert.notNull(ids, "IDs must not be null");
        return Streamable.of(ids)
            .map(findOrThrow(id -> new NoResultException(String.format("No entity w/ id [%s] exists", id))));
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
        Streamable.of(entities).forEach(this::delete);
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
    protected <F> Streamable<T> findAllBy(String propertyName, F value) {
        Assert.notNull(propertyName, "Property name must not be null");
        return Streamable.of(store.values())
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
        Optional<Field> field = getAccessorDeep(entity.getClass(), Class::getDeclaredFields, contains(property));
        if (field.isEmpty()) {
            throw new NoSuchFieldException(
                String.format("Entity [%s] has no property [%s]", entity.getClass().getCanonicalName(), property)
            );
        }

        return Objects.equals(value, field.get().get(entity));
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
        List<T> resultList = findAllBy(propertyName, value).toList();

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
