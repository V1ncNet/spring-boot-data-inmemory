package de.vinado.spring.data.inmemory.repository.config;

/**
 * Defines callback methods to customize the Java-based configuration for in-memory repositories.
 *
 * @author Vincent Nadoll
 */
public interface InMemoryRepositoryConfigurer {

    /**
     * Add identifier pairs to the given registry.
     */
    default void addIdentifiers(IdentifierRegistry registry) {
    }
}
