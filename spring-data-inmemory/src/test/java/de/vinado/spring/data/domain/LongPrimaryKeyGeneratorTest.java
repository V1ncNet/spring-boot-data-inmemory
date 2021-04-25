package de.vinado.spring.data.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Vincent Nadoll
 */
class LongPrimaryKeyGeneratorTest {

    private LongPrimaryKeyGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new LongPrimaryKeyGenerator();
    }

    @Test
    void nextOfNull_shouldStartSequenz() {
        Long next = generator.next(null);

        assertNotNull(next);
        assertEquals(1L, next);
    }

    @Test
    void nonPositivePrevious_shouldStartSequenz() {
        Long next = generator.next(0L);

        assertNotNull(next);
        assertEquals(1, next);

        next = generator.next(-1L);

        assertNotNull(next);
        assertEquals(1, next);
    }

    @Test
    void positivePrevious_shouldIncrement() {
        Long next = generator.next(42L);

        assertNotNull(next);
        assertEquals(43L, next);
    }

}
