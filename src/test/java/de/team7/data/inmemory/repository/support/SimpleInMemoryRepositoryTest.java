package de.team7.data.inmemory.repository.support;

import de.team7.data.domain.PrimaryKeyGenerator;
import de.team7.data.inmemory.repository.EntityStore;
import de.team7.data.repository.NoResultException;
import de.team7.data.repository.NonUniqueResultException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vincent Nadoll
 */
class SimpleInMemoryRepositoryTest {

    private static final int ID = 42;
    private static final PrimaryKeyGenerator<Integer> incrementer = previous -> {
        if (null == previous) {
            return 1;
        }

        return ++previous;
    };

    private EntityStore entityStore;
    private SimpleInMemoryRepository<Entity, Integer> repository;

    @BeforeEach
    void setUp() {
        entityStore = new InMemoryEntityTableStore();
        ((InMemoryEntityTableStore) entityStore).add(new InMemoryEntityStore<>(Entity.class, incrementer));
        repository = new SimpleInMemoryRepository<>(Entity.class, entityStore);
    }

    @Test
    void saveNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void saveEntity_shouldStoreEntity() {
        Entity entity = new Entity(ID);

        Entity saved = repository.save(entity);

        assertNotNull(saved);
        assertEquals(ID, saved.getId());
    }

    @Test
    void saveNullId_shouldStoreEntity() {
        Entity entity = new Entity();

        Entity saved = repository.save(entity);

        assertNotNull(saved);
        assertEquals(1, saved.getId());
    }

    @Test
    void saveNullIdViaMethodAccessor_shouldStoreEntity() {
        InMemoryEntityStore<Integer> entityStore = new InMemoryEntityStore<>(EntityIdGetterAccessor.class, incrementer);
        SimpleInMemoryRepository<EntityIdGetterAccessor, Integer> repository =
            new SimpleInMemoryRepository<>(EntityIdGetterAccessor.class, entityStore);
        EntityIdGetterAccessor entity = new EntityIdGetterAccessor();

        EntityIdGetterAccessor saved = repository.save(entity);

        assertNotNull(saved);
        assertEquals(1, saved.getId());
    }

    @Test
    void missingIdAccessor_shouldThrowException() {
        SimpleInMemoryRepository<Object, Integer> repository =
            new SimpleInMemoryRepository<>(Object.class, entityStore);
        Object entity = new Object();

        assertThrows(IllegalArgumentException.class, () -> repository.save(entity));
    }

    @Test
    void saveNulls_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(null));
    }

    @Test
    void saveDuplicateIds_shouldOverrideExisting() {
        Entity entity = new Entity(ID, "foo");
        repository.save(entity);

        entity = new Entity(ID, "bar");
        entity = repository.save(entity);

        assertNotNull(entity);
        assertEquals(ID, entity.getId());
        assertEquals("bar", entity.getName());
    }

    @Test
    void saveAll_shouldStoreEntities() {
        List<Entity> entities = Arrays.asList(new Entity(ID), new Entity());

        List<Entity> saved = repository.saveAll(entities);

        assertNotNull(saved);
        assertEquals(2, saved.size());
        assertEquals(ID, saved.get(0).getId());
        assertEquals(ID + 1, saved.get(1).getId());
    }

    @Test
    void findByNullId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
    }

    @Test
    void findByNonExistingId_shouldReturnEmptyOptional() {
        Optional<Entity> entity = repository.findById(ID);

        assertNotNull(entity);
        assertTrue(entity.isEmpty());
    }

    @Test
    void saveOnceThenFind_shouldRetrieveSavedEntity() {
        Entity entity = new Entity(ID);

        repository.save(entity);
        Optional<Entity> retrieved = repository.findById(ID);

        assertNotNull(entity);
        assertTrue(retrieved.isPresent());
        assertEquals(ID, retrieved.get().getId());
    }

    @Test
    void existByNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.existsById(null));
    }

    @Test
    void emptyRepository_shouldReturnFalse() {
        boolean exist = repository.existsById(ID);

        assertFalse(exist);
    }

    @Test
    void saveOnceThenExist_shouldReturnFalse() {
        Entity entity = new Entity(ID);

        repository.save(entity);
        boolean exist = repository.existsById(1337);

        assertFalse(exist);
    }

    @Test
    void saveOnceThenExist_shouldReturnTrue() {
        Entity entity = new Entity(ID);

        repository.save(entity);
        boolean exist = repository.existsById(ID);

        assertTrue(exist);
    }

    @Test
    void emptyRepository_shouldReturnEmptyList() {
        List<Entity> list = repository.findAll().toList();

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    void saveOnceThenList_shouldReturnOneEntity() {
        Entity entity = new Entity();

        repository.save(entity);
        List<Entity> list = repository.findAll().toList();

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void listNullIds_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.findAllById(null));
    }

    @Test
    void emptyRepositoryThenListById_shouldThrowException() {
        List<Integer> ids = Collections.singletonList(ID);

        assertThrows(NoResultException.class, () -> repository.findAllById(ids).toSet());
    }

    @Test
    void saveMultipleThenListOtherIds_shouldThrowException() {
        List<Entity> entities = Arrays.asList(new Entity(ID), new Entity());

        repository.saveAll(entities);

        assertThrows(NoResultException.class, () -> repository.findAllById(Arrays.asList(ID, 1337)).toSet());
    }

    @Test
    void saveMultipleThenListById_shouldRetrieveMultiple() {
        int ID1 = 1337;
        List<Entity> entities = Arrays.asList(new Entity(ID), new Entity(ID1));

        repository.saveAll(entities);
        List<Entity> retrieved = repository.findAllById(Arrays.asList(ID, ID1)).toList();

        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        assertEquals(ID, retrieved.get(0).getId());
        assertEquals(ID1, retrieved.get(1).getId());
    }

    @Test
    void emptyRepository_shouldBeEmpty() {
        assertEquals(0, repository.count());
    }

    @Test
    void saveOnceThenCount_shouldReturnOne() {
        Entity entity = new Entity();

        repository.save(entity);

        assertEquals(1, repository.count());
    }

    @Test
    void deleteNullId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.deleteById(null));
    }

    @Test
    void deleteIdOnEmptyRepository_shouldThrowException() {
        assertThrows(NoResultException.class, () -> repository.deleteById(ID));
    }

    @Test
    void saveOnceThenDeleteId_shouldDeleteEntity() {
        Entity entity = new Entity(ID);

        repository.save(entity);
        repository.deleteById(ID);

        assertEquals(0, repository.count());
    }

    @Test
    void deleteNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.delete(null));
    }

    @Test
    void deleteNewEntity_shouldDoNothing() {
        SimpleInMemoryRepository<EntityIdGetterAccessor, Integer> repository =
            new SimpleInMemoryRepository<>(EntityIdGetterAccessor.class, entityStore);
        EntityIdGetterAccessor entity = new EntityIdGetterAccessor();

        repository.delete(entity);
    }

    @Test
    void saveOnceThenDeleteOther_shouldDoNothing() {
        int ID2 = 1337;
        Entity entity = new Entity(ID);

        repository.save(entity);
        repository.delete(new Entity(ID2));

        assertEquals(1, repository.count());
    }

    @Test
    void saveOnceThenDelete_shouldDeleteEntity() {
        Entity entity = new Entity();

        repository.save(entity);
        repository.delete(entity);

        assertEquals(0, repository.count());
    }

    @Test
    void deleteNullIds_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.deleteAll(null));
    }

    @Test
    void deleteIdsOnEmptyRepository_shouldDoNothing() {
        List<Entity> entities = Collections.singletonList(new Entity());
        repository.deleteAll(entities);
    }

    @Test
    void saveMultipleThenDelete_shouldDeleteMultiple() {
        Entity e1 = new Entity(ID);
        Entity e2 = new Entity(1337);
        Entity e3 = new Entity(420);

        repository.saveAll(Arrays.asList(e1, e2, e3));
        repository.deleteAll(Arrays.asList(e1, e2));

        assertEquals(1, repository.count());
        assertTrue(repository.existsById(e3.getId()));
    }

    @Test
    void saveMultipleThenDeleteAll_shouldClearRepository() {
        List<Entity> entities = Arrays.asList(new Entity(1337), new Entity(420));

        repository.saveAll(entities);
        repository.deleteAll();

        assertEquals(0, repository.count());
    }

    @Test
    void findAllByNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.findAllBy(null, null));
    }

    @Test
    void saveOnceTheFindBy_shouldThrowException() {
        Entity entity = new Entity();

        repository.save(entity);
        assertThrows(RuntimeException.class, () -> repository.findAllBy("foo", null).toSet());
    }

    @Test
    void findAllInEmptyRepository_shouldRetrieveEmptyResult() {
        Stream<Entity> resultStream = repository.findAllBy("foo", null).stream();

        assertNotNull(resultStream);
        assertEquals(0, resultStream.count());
    }

    @Test
    void saveMultipleThenFindBy_shouldRetrieveSubset() {
        List<Entity> entities = Arrays.asList(
            new Entity(null, "foo"),
            new Entity(null, "foo"),
            new Entity(null, "bar")
        );

        repository.saveAll(entities);
        List<Entity> resultStream = repository.findAllBy("name", "foo").toList();

        assertEquals(3, repository.count());
        assertEquals(2, resultStream.size());
    }

    @Test
    void findUniqueByNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.findUniqueBy(null, null));
    }

    @Test
    void saveDuplicateThenRetrieveUnique_shouldThrowException() {
        List<Entity> entities = Arrays.asList(new Entity(420, "foo"), new Entity(1337, "foo"));

        repository.saveAll(entities);

        assertThrows(NonUniqueResultException.class, () -> repository.findUniqueBy("name", "foo"));
    }

    @Test
    void saveOnceTheRetrieveUnique_shouldReturnedStoredEntity() {
        Entity entity = new Entity(ID, "foo");

        repository.save(entity);
        Optional<Entity> retrieved = repository.findUniqueBy("name", "foo");

        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        assertEquals("foo", retrieved.get().getName());
    }

    @Test
    void saveOnceThenRetrieveUniqueByNull_shouldReturnStoredEntity() {
        Entity entity = new Entity(ID, null);

        repository.save(entity);
        Optional<Entity> retrieved = repository.findUniqueBy("name", null);

        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        assertNull(retrieved.get().getName());
    }

    @Test
    void saveOnceThenRetrieveUnique_shouldReturnEmptyOptional() {
        Entity entity = new Entity(ID, "bar");

        repository.save(entity);
        Optional<Entity> retrieved = repository.findUniqueBy("name", "foo");

        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
    }

    @Test
    void saveMultipleNullIds_shouldAutoIncrementId() {
        List<Entity> entities = Arrays.asList(new Entity(), new Entity(), new Entity());

        repository.saveAll(entities);
        List<Integer> savedIds = repository.findAll()
            .stream()
            .map(Entity::getId)
            .sorted()
            .collect(Collectors.toList());

        assertEquals(3, savedIds.size());
        assertEquals(1, savedIds.get(0));
        assertEquals(2, savedIds.get(1));
        assertEquals(3, savedIds.get(2));
    }

    @Test
    void saveOnceThenDeleteThenSaveTwice_shouldAutoIncrementIds() {
        Entity once = new Entity();

        once = repository.save(once);
        repository.delete(once);

        Entity twice = new Entity();

        twice = repository.save(twice);

        assertNotNull(twice);
        assertEquals(2, twice.getId());
    }

    @Data
    @NoArgsConstructor(force = true)
    @RequiredArgsConstructor
    @AllArgsConstructor
    static class Entity {

        @Id
        @Nullable
        private final Integer id;
        private String name;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    static class SubEntity extends Entity {

        private int quantity;

        public SubEntity(Integer id) {
            this(id, null, 0);
        }

        public SubEntity(Integer id, String name) {
            this(id, name, 0);
        }

        public SubEntity(Integer id, String name, int quantity) {
            super(id, name);
            this.quantity = quantity;
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
