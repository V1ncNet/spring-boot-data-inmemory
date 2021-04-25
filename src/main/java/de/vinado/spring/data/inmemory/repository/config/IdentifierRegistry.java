package de.vinado.spring.data.inmemory.repository.config;

import de.vinado.spring.data.domain.PrimaryKeyGenerator;
import lombok.NonNull;

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
     * @param idClass             must not be {@literal null}
     * @param primaryKeyGenerator must not be {@literal null}
     * @param <ID>                the typ of the identifier
     */
    public <ID> void addIdentifier(@NonNull Class<ID> idClass,
                                   @NonNull PrimaryKeyGenerator<ID> primaryKeyGenerator) {
        registrations.put(idClass, primaryKeyGenerator);
    }

    /**
     * Returns a mapping of all registrations.
     */
    @Override
    public IdentifierMapping get() {
        return new SimpleIdentifierMapping(registrations);
    }
}
