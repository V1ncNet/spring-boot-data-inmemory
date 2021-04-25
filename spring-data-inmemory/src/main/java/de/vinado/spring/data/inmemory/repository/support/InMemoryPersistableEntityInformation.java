package de.vinado.spring.data.inmemory.repository.support;

import org.springframework.data.domain.Persistable;

/**
 * Extension of {@link InMemoryEntityInformation} that considers methods of {@link Persistable} to lookup the ID.
 *
 * @author Vincent Nadoll
 */
public class InMemoryPersistableEntityInformation<T extends Persistable<ID>, ID>
    extends InMemoryEntityInformation<T, ID> {

    public InMemoryPersistableEntityInformation(Class<T> domainClass) {
        super(domainClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNew(T entity) {
        return entity.isNew();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ID getId(T entity) {
        return entity.getId();
    }
}
