package de.team7.data.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Vincent Nadoll
 */
class IntegerPrimaryKeyGeneratorTest {

    private IntegerPrimaryKeyGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new IntegerPrimaryKeyGenerator();
    }

    @Test
    void nextOfNull_shouldStartSequenz() {
        Integer next = generator.next(null);

        assertNotNull(next);
        assertEquals(1, next);
    }

    @Test
    void nonPositivePrevious_shouldStartSequenz() {
        Integer next = generator.next(0);

        assertNotNull(next);
        assertEquals(1, next);

        next = generator.next(-1);

        assertNotNull(next);
        assertEquals(1, next);
    }

    @Test
    void positivePrevious_shouldIncrement() {
        Integer next = generator.next(42);

        assertNotNull(next);
        assertEquals(43, next);
    }
}
