package de.team7.data.inmemory.repository.config;

import de.team7.data.domain.PrimaryKeyGenerator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Vincent Nadoll
 */
class InMemoryRepositoryConfigurerCompositeTest {

    @Test
    void addConfigure_shouldFillRegistry() {
        InMemoryRepositoryConfigurerComposite composite = new InMemoryRepositoryConfigurerComposite();

        PrimaryKeyGenerator<Integer> intGenerator = PrimaryKeyGenerator.identity();
        PrimaryKeyGenerator<Long> longGenerator = PrimaryKeyGenerator.identity();

        IdentifierRegistry registry = new IdentifierRegistry();
        registry.addIdentifier(Integer.class, intGenerator);

        InMemoryRepositoryConfigurer configurer = new InMemoryRepositoryConfigurer() {
            @Override
            public void addIdentifiers(IdentifierRegistry registry) {
                registry.addIdentifier(Long.class, longGenerator);
            }
        };
        configurer.addIdentifiers(registry);

        composite.addInMemoryRepositoryConfigurers(Collections.singleton(configurer));
        composite.addIdentifiers(registry);

        IdentifierMapping mapping = registry.get();
        Map<Class<?>, PrimaryKeyGenerator<?>> map = mapping.get();

        assertEquals(2, map.size());
        assertEquals(intGenerator, map.get(Integer.class));
        assertEquals(longGenerator, map.get(Long.class));
    }
}
