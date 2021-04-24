package de.team7.data.inmemory.repository.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Collection of functions to interact with entity identifiers.
 *
 * @author Vincent Nadoll
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdUtils {

    /**
     * Returns the ID of the given entity which must contain an accessor annotated with {@link Id}.
     *
     * @param entity must not be {@literal null}
     * @return the entity's ID
     * @throws IllegalArgumentException in case the entity doesn't contain an applicable id accessor
     */
    @Nullable
    public static Object getId(@NonNull Object entity) {
        try {
            Class<?> entityClass = entity.getClass();

            Optional<Field> field = getIdField(entityClass);
            if (field.isPresent()) {
                Field idField = field.get();
                return idField.get(entity);
            }

            Optional<Method> method = getIdGetter(entityClass);
            if (method.isPresent()) {
                Method idGetter = method.get();
                return idGetter.invoke(entity);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(
                String.format("Couldn't get id accessor from entity [%s]", entity.getClass().getCanonicalName()), e
            );
        }

        throw new IllegalArgumentException(String.format(
            "Entity [%s] has neither field nor method annotated with @Id", entity.getClass().getCanonicalName()
        ));
    }

    static void setId(@NonNull Object entity, @NonNull Object id) {
        try {
            Class<?> entityClass = entity.getClass();

            Optional<Field> field = getIdField(entityClass);
            if (field.isPresent()) {
                Field idField = field.get();
                idField.set(entity, id);
                return;
            }

            Optional<Method> idGetter = getIdGetter(entityClass);
            if (idGetter.isPresent()) {
                Method idSetter = getIdSetter(entityClass, id);
                idSetter.invoke(entity, id);
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                String.format("Couldn't set id in entity [%s]", entity.getClass().getCanonicalName()), e
            );
        }
    }

    static Optional<Field> getIdField(Class<?> domainClass) {
        return getAccessorDeep(
            domainClass,
            Class::getDeclaredFields,
            IdUtils::hasIdAnnotation
        );
    }

    static Optional<Method> getIdGetter(Class<?> domainClass) {
        return getAccessorDeep(
            domainClass,
            Class::getDeclaredMethods,
            IdUtils::hasIdAnnotation
        );
    }

    private static boolean hasIdAnnotation(AccessibleObject accessible) {
        return Objects.nonNull(accessible.getAnnotation(Id.class));
    }

    static <M extends AccessibleObject> Optional<M> getAccessorDeep(Class<?> domainClass,
                                                                    Function<Class<?>, M[]> accessor,
                                                                    Predicate<M> accessorMatcher) {
        for (Class<?> entityClass = domainClass; null != entityClass; entityClass = entityClass.getSuperclass()) {
            Optional<M> member = getAccessor(entityClass, accessor, accessorMatcher);
            if (member.isPresent()) {
                return member;
            }
        }

        return Optional.empty();
    }

    static <M extends AccessibleObject> Optional<M> getAccessor(Class<?> entityClass, Function<Class<?>, M[]> accessors,
                                                                Predicate<M> accessorMatcher) {
        for (M member : accessors.apply(entityClass)) {
            if (accessorMatcher.test(member)) {
                member.setAccessible(true);
                return Optional.of(member);
            }
        }

        return Optional.empty();
    }

    private static Method getIdSetter(Class<?> domainClass, Object id) throws NoSuchMethodException {
        Method idGetter = getIdGetter(domainClass).orElseThrow(NoSuchMethodException::new);
        String setterName = idGetter.getName().replaceFirst("^get", "set");
        Method setter = domainClass.getMethod(setterName, id.getClass());
        setter.setAccessible(true);
        return setter;
    }
}
