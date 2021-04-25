package de.vinado.spring.data.inmemory.repository.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;

import java.util.UUID;

import static de.vinado.spring.data.inmemory.repository.support.IdUtils.getId;
import static de.vinado.spring.data.inmemory.repository.support.IdUtils.setId;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vincent Nadoll
 */
class IdUtilsTest {

    @Test
    void getNullEntity_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> getId(null));
    }

    @Test
    void getIdFromInvalidEntity_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> getId(new MissingIdAnnotationEntity(6)));
        assertThrows(IllegalArgumentException.class, () -> getId(new NotAnEntity()));
    }

    @Test
    void getNullIdField_shouldReturnNull() {
        FieldEntity entity = new FieldEntity(null);

        assertNullId(entity);
    }

    @Test
    void getNullIdMethod_shouldReturnNull() {
        MethodEntity entity = new MethodEntity(null);

        assertNullId(entity);
    }

    private static void assertNullId(Object entity) {
        Object id = getId(entity);

        assertNull(id);
    }

    @Test
    void getIdField_shouldReturnId() {
        FieldEntity entity = new FieldEntity(42);

        assertIntegerId(entity, 42);
    }

    @Test
    void getIdMethod_shouldReturnId() {
        MethodEntity entity = new MethodEntity(42);

        assertIntegerId(entity, 42);
    }

    private static void assertIntegerId(Object entity, Integer expected) {
        Object id = getId(entity);

        assertNotNull(id);
        assertTrue(id instanceof Integer);
        assertEquals(expected, id);
    }

    @Test
    void setNullArguments_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> setId(null, null));
        assertThrows(IllegalArgumentException.class, () -> setId(new Object(), null));
        assertThrows(IllegalArgumentException.class, () -> setId(null, 42));
    }

    @Test
    void setIncompatibleType_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> setId(new FieldEntity(null), UUID.randomUUID().toString()));
        assertThrows(IllegalArgumentException.class, () -> setId(new MethodEntity(null), UUID.randomUUID().toString()));
    }

    @Test
    void setIdToInvalidEntity_shouldDoNoting() {
        assertDoesNotThrow(() -> setId(new MissingIdAnnotationEntity(6), 1337));
        assertDoesNotThrow(() -> setId(new NotAnEntity(), 1337));
    }

    @Test
    void setIdField_shouldSetId() {
        FieldEntity entity = new FieldEntity(null);

        setId(entity, 42);
        Integer id = entity.getId();

        assertNotNull(id);
        assertEquals(42, id);
    }

    @Test
    void setIdMethod_shouldSetId() {
        MethodEntity entity = new MethodEntity(null);

        setId(entity, 42);
        Integer id = entity.getId();

        assertNotNull(id);
        assertEquals(42, id);
    }

    @Data
    static class FieldEntity {
        @Id
        private final Integer id;
    }

    @Data
    @AllArgsConstructor
    static class MethodEntity {
        @Getter(onMethod_ = @Id)
        private Integer id;
    }

    @Data
    static class NotAnEntity {
    }

    @Data
    static class MissingIdAnnotationEntity {
        private final Integer id;
    }
}
