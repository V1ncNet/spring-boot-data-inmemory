package de.vinado.boot.autoconfigure.data.inmemory;

import de.vinado.spring.data.inmemory.repository.config.EnableInMemoryRepositories;
import de.vinado.spring.data.inmemory.repository.config.InMemoryRepositoryConfigExtension;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar} used to auto-configure in-memory
 * repositories.
 *
 * @author Vincent Nadoll
 */
class InMemoryRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableInMemoryRepositories.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getConfiguration() {
        return EnableInMemoryRepositoriesConfiguration.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new InMemoryRepositoryConfigExtension();
    }

    @EnableInMemoryRepositories
    private static class EnableInMemoryRepositoriesConfiguration {
    }
}
