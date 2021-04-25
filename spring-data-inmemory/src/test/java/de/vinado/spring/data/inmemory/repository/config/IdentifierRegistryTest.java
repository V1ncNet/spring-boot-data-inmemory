package de.vinado.spring.data.inmemory.repository.config;

import de.vinado.spring.data.domain.PrimaryKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vincent Nadoll
 */
class IdentifierRegistryTest {

    private IdentifierRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new IdentifierRegistry();
    }

    @Test
    void addNullArguments_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> registry.addIdentifier(null, previous -> previous));
        assertThrows(IllegalArgumentException.class, () -> registry.addIdentifier(Object.class, null));
    }

    @Test
    void emptyRegistry_shouldReturnEmptyMapping() {
        IdentifierMapping mapping = registry.get();

        assertNotNull(mapping);
        assertNotNull(mapping.get());
        assertTrue(mapping.get().isEmpty());
    }

    @Test
    void addOnceTheGet_shouldReturnOne() {
        PrimaryKeyGenerator<Integer> generator = PrimaryKeyGenerator.identity();
        registry.addIdentifier(Integer.class, generator);

        IdentifierMapping mapping = registry.get();

        assertNotNull(mapping);
        assertNotNull(mapping.get());
        assertFalse(mapping.get().isEmpty());
        assertEquals(generator, mapping.get().get(Integer.class));
    }
}
