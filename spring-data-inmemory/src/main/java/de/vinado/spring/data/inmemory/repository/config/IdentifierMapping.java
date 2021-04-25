package de.vinado.spring.data.inmemory.repository.config;

import de.vinado.spring.data.domain.PrimaryKeyGenerator;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A mapping supplier which holds references between domain classes and their PK generation strategy.
 *
 * @author Vincent Nadoll
 */
public interface IdentifierMapping extends Supplier<Map<Class<?>, PrimaryKeyGenerator<?>>> {
}
