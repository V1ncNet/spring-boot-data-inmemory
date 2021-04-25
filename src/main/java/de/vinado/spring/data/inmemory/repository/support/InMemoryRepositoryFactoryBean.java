package de.vinado.spring.data.inmemory.repository.support;

import de.vinado.spring.data.inmemory.repository.EntityStore;
import de.vinado.spring.data.inmemory.repository.config.IdentifierMapping;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

/**
 * Special adapter for Springs {@link org.springframework.beans.factory.FactoryBean} interface to allow easy setup of
 * repository factories via Spring configuration.
 *
 * @param <T>  the type of the repository
 * @param <S>  the type of the entity
 * @param <ID> the type of the entity's identifier
 * @author Vincent Nadoll
 */
public class InMemoryRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
    extends RepositoryFactoryBeanSupport<T, S, ID> {

    @Setter(onMethod_ = @Autowired)
    private EntityStore entityStore;

    @Setter(onMethod_ = @Autowired(required = false))
    private IdentifierMapping identifierMapping;

    /**
     * Creates a new {@link RepositoryFactoryBeanSupport} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    protected InMemoryRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        Assert.notNull(entityStore, "Entity manager must not be null");
        return new InMemoryRepositoryFactory(entityStore, identifierMapping);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(entityStore, "Entity manager must not be null");
        super.afterPropertiesSet();
    }
}
