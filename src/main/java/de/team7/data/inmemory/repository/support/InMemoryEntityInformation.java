package de.team7.data.inmemory.repository.support;

import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.Assert;

import java.util.Optional;

import static de.team7.data.inmemory.repository.support.IdUtils.getIdField;
import static de.team7.data.inmemory.repository.support.IdUtils.getIdGetter;

/**
 * Entity information base class which obtains its information via refection.
 *
 * @author Vincent Nadoll
 * @see IdUtils
 */
public class InMemoryEntityInformation<T, ID> extends AbstractEntityInformation<T, ID> {

    private final Class<T> domainClass;

    /**
     * Creates a new {@link InMemoryEntityInformation} with the given domain class.
     *
     * @param domainClass must not be {@literal null}.
     * @deprecated use {@link #getEntityInformation(Class)} instead
     */
    @Deprecated
    @SuppressWarnings("all")
    public InMemoryEntityInformation(Class<T> domainClass) {
        super(domainClass);
        this.domainClass = domainClass;
    }

    /**
     * Creates {@link InMemoryEntityInformation} for the given domain class.
     *
     * @param domainClass must not be {@code null}
     * @param <T>         the type of the domain class
     * @return new instance of this class
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> InMemoryEntityInformation<T, ?> getEntityInformation(Class<T> domainClass) {
        Assert.notNull(domainClass, "Domain class must not be null!");

        if (Persistable.class.isAssignableFrom(domainClass)) {
            return new InMemoryPersistableEntityInformation(domainClass);
        } else {
            return new InMemoryEntityInformation(domainClass);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ID getId(T entity) {
        return (ID) IdUtils.getId(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ID> getIdType() {
        return getIdFieldType().or(this::getIdGetterReturnType)
            .orElseThrow(IllegalArgumentException::new);
    }

    @SuppressWarnings("unchecked")
    private Optional<Class<ID>> getIdFieldType() {
        return getIdField(domainClass)
            .map(field -> (Class<ID>) field.getType());
    }

    @SuppressWarnings("unchecked")
    private Optional<Class<ID>> getIdGetterReturnType() {
        return getIdGetter(domainClass)
            .map(method -> (Class<ID>) method.getReturnType());
    }
}
