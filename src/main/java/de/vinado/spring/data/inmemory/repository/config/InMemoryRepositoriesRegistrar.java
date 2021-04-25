package de.vinado.spring.data.inmemory.repository.config;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar} to enable the
 * {@link EnableInMemoryRepositories} annotation.
 *
 * @author Vincent Nadoll
 */
public class InMemoryRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

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
    protected RepositoryConfigurationExtension getExtension() {
        return new InMemoryRepositoryConfigExtension();
    }
}
