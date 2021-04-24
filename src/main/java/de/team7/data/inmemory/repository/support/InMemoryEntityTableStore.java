package de.team7.data.inmemory.repository.support;

import de.team7.data.inmemory.repository.EntityStore;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A composite of typed entity stores aka. tables which makes this class acting as a (stateful) in memory-database.
 *
 * @author Vincent Nadoll
 */
@AllArgsConstructor
public class InMemoryEntityTableStore implements EntityStore {

    private final Map<Class<?>, EntityStore> stores;

    public InMemoryEntityTableStore() {
        this.stores = new HashMap<>();
    }

    <ID> void add(@NonNull InMemoryEntityStore<ID> store) {
        stores.put(store.getDomainClass(), store);
    }

    @Override
    public void store(Object entity) {
        EntityStore store = getTable(entity);
        store.store(entity);
    }

    @Override
    public <T> T merge(T entity) {
        EntityStore store = getTable(entity);
        return store.merge(entity);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        EntityStore store = getTable(entityClass);
        return store.find(entityClass, primaryKey);
    }

    @Override
    public <T> Stream<T> findAll(Class<T> entityClass) {
        EntityStore store = getTable(entityClass);
        return store.findAll(entityClass);
    }

    @Override
    public void remove(Object entity) {
        EntityStore store = getTable(entity);
        store.remove(entity);
    }

    @Override
    public boolean contains(Object entity) {
        EntityStore store = getTable(entity);
        return store.contains(entity);
    }

    private EntityStore getTable(Object entity) {
        Class<?> type = ClassUtils.getUserClass(entity);
        EntityStore entityStore = stores.get(type);
        if (null == entityStore) {
            throw new IllegalArgumentException(
                String.format("[%s] is not an entity or is not managed by the entity store",
                    entity.getClass().getSimpleName()
                )
            );
        }

        return entityStore;
    }

    private EntityStore getTable(Class<?> entityClass) {
        Class<?> type = ClassUtils.getUserClass(entityClass);
        EntityStore entityStore = stores.get(type);
        if (null == entityStore) {
            throw new IllegalArgumentException(
                String.format("[%s] is not an entity or is not managed by the entity store",
                    entityClass.getSimpleName()
                )
            );
        }

        return entityStore;
    }
}
