package de.team7.data.inmemory.repository.support;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vincent Nadoll
 */
class InMemoryPersistableEntityInformationTest {

    private InMemoryPersistableEntityInformation<Entity, Integer> information;

    @BeforeEach
    void setUp() {
        information = new InMemoryPersistableEntityInformation<>(Entity.class);
    }

    @Test
    void initializeNullArguments_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryEntityInformation<>(null));
    }

    @Test
    void nullIdEntity_shouldBeNew() {
        Entity entity = new Entity();

        assertTrue(information.isNew(entity));
    }

    @Test
    void nonNullIdEntity_shouldNotBeNew() {
        Entity entity = new Entity(2);

        assertFalse(information.isNew(entity));
    }

    @Test
    void nullIdEntity_shouldReturnNull() {
        Entity entity = new Entity();

        Integer id = information.getId(entity);

        assertNull(id);
    }

    @Test
    void nonNullEntity_shouldReturnId() {
        Entity entity = new Entity(1);

        Integer id = information.getId(entity);

        assertNotNull(id);
        assertEquals(1, id);
    }

    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    static class Entity implements Persistable<Integer> {

        @Id
        private final Integer id;

        @Override
        public boolean isNew() {
            return Objects.isNull(id);
        }
    }
}
