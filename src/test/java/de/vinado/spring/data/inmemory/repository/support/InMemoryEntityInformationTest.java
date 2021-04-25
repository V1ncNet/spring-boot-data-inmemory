package de.vinado.spring.data.inmemory.repository.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Vincent Nadoll
 */
class InMemoryEntityInformationTest {

    private InMemoryEntityInformation<Entity, Integer> information;

    @BeforeEach
    void setUp() {
        information = new InMemoryEntityInformation<>(Entity.class);
    }

    @Test
    void initializeNullArguments_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryEntityInformation<>(null));
    }

    @Test
    void entityInformation_shouldReturnInteger() {
        Class<Integer> idType = information.getIdType();

        assertNotNull(idType);
        assertEquals(Integer.class, idType);
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

    @Test
    void entityCreatingInformation_shouldCreateGenericInMemoryEntityInformation() {
        InMemoryEntityInformation<Entity, ?> information = InMemoryEntityInformation.getEntityInformation(Entity.class);

        assertNotNull(information);
        assertEquals(InMemoryEntityInformation.class, information.getClass());
    }

    @Test
    void persistableEntityCreatingInformation_shouldCreatePersistableInMemoryEntityInformation() {
        InMemoryEntityInformation<PersistableEntity, ?> information =
            InMemoryEntityInformation.getEntityInformation(PersistableEntity.class);

        assertNotNull(information);
        assertEquals(InMemoryPersistableEntityInformation.class, information.getClass());
    }

    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    static class Entity {

        @Id
        private final Integer id;
    }

    static class PersistableEntity extends Entity implements Persistable<Integer> {

        @Override
        public boolean isNew() {
            return Objects.isNull(getId());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class EntityIdGetterAccessor {

        @Getter(onMethod_ = @Id)
        private int id;
    }
}
