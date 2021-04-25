package de.vinado.spring.data.inmemory.repository.config;

import de.vinado.spring.data.domain.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;

/**
 * Simple implementation of the mapping supplier which functions as a wrapper to the containing map.
 *
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class SimpleIdentifierMapping implements IdentifierMapping {

    private final Map<Class<?>, PrimaryKeyGenerator<?>> mapping;

    @Override
    public Map<Class<?>, PrimaryKeyGenerator<?>> get() {
        return Collections.unmodifiableMap(mapping);
    }
}
