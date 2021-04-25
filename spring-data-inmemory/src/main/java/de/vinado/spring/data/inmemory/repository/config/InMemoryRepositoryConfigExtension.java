package de.vinado.spring.data.inmemory.repository.config;

import de.vinado.spring.data.inmemory.Entity;
import de.vinado.spring.data.inmemory.repository.InMemoryRepository;
import de.vinado.spring.data.inmemory.repository.support.InMemoryRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * In-memory configuration extension parsing custom attributes from {@link EnableInMemoryRepositories}.
 *
 * @author Vincent Nadoll
 */
public class InMemoryRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModulePrefix() {
        return "inmemory";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModuleName() {
        return "in-memory";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepositoryFactoryBeanClassName() {
        return InMemoryRepositoryFactoryBean.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.singleton(Entity.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.singleton(InMemoryRepository.class);
    }
}
