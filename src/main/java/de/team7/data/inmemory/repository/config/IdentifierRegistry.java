package de.team7.data.inmemory.repository.config;

import de.team7.data.domain.PrimaryKeyGenerator;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Stores registrations of domain classes to their PK generation strategy.
 *
 * @author Vincent Nadoll
 */
public class IdentifierRegistry implements Supplier<IdentifierMapping> {

    private final Map<Class<?>, PrimaryKeyGenerator<?>> registrations = new HashMap<>();

    /**
     * Registers a new pair.
     *
     * @param entityClass         must not be {@literal null}
     * @param primaryKeyGenerator must not be {@literal null}
     */
    public void addIdentifier(@NonNull Class<?> entityClass, @NonNull PrimaryKeyGenerator<?> primaryKeyGenerator) {
        registrations.put(entityClass, primaryKeyGenerator);
    }

    /**
     * Returns a mapping of all registrations.
     */
    @Override
    public IdentifierMapping get() {
        return new SimpleIdentifierMapping(registrations);
    }
}
