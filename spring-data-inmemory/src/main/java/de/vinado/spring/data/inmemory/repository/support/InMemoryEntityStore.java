package de.vinado.spring.data.inmemory.repository.support;

import de.vinado.spring.data.domain.PrimaryKeyGenerator;
import de.vinado.spring.data.inmemory.repository.EntityStore;
import de.vinado.spring.data.repository.EntityExistsException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static de.vinado.spring.data.inmemory.repository.support.IdUtils.getId;
import static de.vinado.spring.data.inmemory.repository.support.IdUtils.setId;

/**
 * An in-memory implementation of {@literal EntityManager}. It stores its values in a {@literal HashMap}.
 *
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class InMemoryEntityStore<ID> implements EntityStore {

    private final Map<ID, Object> store = new HashMap<>();

    @NonNull
    @Getter(AccessLevel.PACKAGE)
    private final Class<?> domainClass;

    @NonNull
    private final PrimaryKeyGenerator<ID> primaryKeyGenerator;

    private ID previousId;

    @Override
    public void store(Object entity) {
        validateIsEntity(entity);

        if (contains(entity)) {
            throw new EntityExistsException(
                String.format(
                    "Store already contains entity of type [%s] with the same ID",
                    entity.getClass().getCanonicalName()
                )
            );
        }

        ID entityId = primaryKeyGenerator.next(previousId);
        setId(entity, entityId);
        store.put(entityId, entity);
        previousId = entityId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T merge(T entity) {
        validateIsEntity(entity);

        ID entityId = (ID) getId(entity);
        store.put(entityId, entity);
        previousId = entityId;
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void remove(Object entity) {
        validateIsEntity(entity);

        if (!contains(entity)) {
            throw new IllegalArgumentException(
                String.format("Store doesn't contain such entity of type [%s]", entity.getClass().getCanonicalName())
            );
        }

        store.remove((ID) getId(entity));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        validateIsEntity(entityClass);

        return entityClass.cast(store.get((ID) primaryKey));
    }

    @Override
    public <T> Stream<T> findAll(Class<T> entityClass) {
        validateIsEntity(entityClass);

        return store.values().stream()
            .map(entityClass::cast);
    }

    @Override
    public boolean contains(Object entity) {
        validateIsEntity(entity);

        return store.containsValue(entity);
    }

    private <T> void validateIsEntity(T entity) {
        if (!domainClass.isInstance(entity)) {
            String message = String.format(
                "[%s] is not an entity of type [%s]",
                entity.getClass().getCanonicalName(),
                domainClass.getSimpleName()
            );
            throw new IllegalArgumentException(message);
        }
    }

    private <T> void validateIsEntity(Class<T> entityClass) {
        if (!domainClass.isAssignableFrom(entityClass)) {
            String message = String.format(
                "[%s] is not an entity of type [%s]",
                entityClass.getCanonicalName(),
                domainClass.getSimpleName()
            );
            throw new IllegalArgumentException(message);
        }
    }
}
