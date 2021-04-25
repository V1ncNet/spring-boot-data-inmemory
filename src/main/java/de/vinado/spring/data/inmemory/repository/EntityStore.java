package de.vinado.spring.data.inmemory.repository;

import java.util.stream.Stream;

/**
 * Interface for managing entities.
 *
 * @author Vincent Nadoll
 */
public interface EntityStore {

    /**
     * Creates a new entity.
     *
     * @param entity must not be {@literal null}
     * @throws de.vinado.spring.data.repository.EntityExistsException in case the entity already exists
     * @throws IllegalArgumentException                       in case the object is not an entity
     */
    void store(Object entity);

    /**
     * Merges the entity with an existing entity.
     *
     * @param entity must not be {@literal null}
     * @param <T>    the type of the entity
     * @return the update entity
     * @throws IllegalArgumentException in case the object is not an entity or has been removed
     */
    <T> T merge(T entity);

    /**
     * Finds an entity by its type and primary key.
     *
     * @param entityClass must not be {@literal null}
     * @param primaryKey  must not be {@literal null}
     * @param <T>         the type of the entity
     * @return the found entity or {@literal null} if none found
     * @throws IllegalArgumentException in case the given class is not an entity class
     */
    <T> T find(Class<T> entityClass, Object primaryKey);

    /**
     * Finds all entities by its type.
     *
     * @param entityClass must not be {@literal null}
     * @param <T>         the type of the entity
     * @return stream of all applicable entities or {@literal Stream.empty()} if none found
     * @throws IllegalArgumentException in case the given class is not an entity class
     */
    <T> Stream<T> findAll(Class<T> entityClass);

    /**
     * Removes the entity.
     *
     * @param entity must not be {@literal null}
     * @throws IllegalArgumentException in case the object is not an entity or has already been removed
     */
    void remove(Object entity);

    /**
     * Returns whether an entity exist.
     *
     * @param entity must not be {@literal null}
     * @return {@literal true} if the entity exist; {@literal false otherwise}
     * @throws IllegalArgumentException in case the object is not an entity
     */
    boolean contains(Object entity);
}
