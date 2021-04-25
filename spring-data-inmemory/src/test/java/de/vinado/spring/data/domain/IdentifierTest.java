package de.vinado.spring.data.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vincent Nadoll
 */
class IdentifierTest {

    private Identifier id;

    @BeforeEach
    void setUp() {
        id = new IdentifierImpl();
    }

    @Test
    void defaultConstructor_shouldContainValue() {
        assertNotNull(id);
        assertNotNull(id.value());
    }

    @Test
    void toString_shouldBeAUuid() {
        assertDoesNotThrow(() -> UUID.fromString(id.toString()));
    }

    @Test
    void equals_shouldEvaluateValue() {
        UUID value = id.value();

        assertTrue(id.equals(IdentifierImpl.of(value)));
    }

    @Test
    void hashCode_shouldEvaluateValue() {
        UUID value = id.value();

        assertEquals(id.hashCode(), IdentifierImpl.of(value).hashCode());
    }

    @Test
    void compareTo_shouldCompareValue() {
        UUID value = UUID.randomUUID();
        Identifier id = new IdentifierImpl(value);
        Identifier id2 = new IdentifierImpl(value);

        assertEquals(0, id.compareTo(id2));
    }

    static class IdentifierImpl extends Identifier {

        IdentifierImpl() {
            super();
        }

        private IdentifierImpl(UUID id) {
            super(id);
        }

        static IdentifierImpl of(UUID id) {
            return new IdentifierImpl(id);
        }
    }
}
