package de.team7.autoconfigure.data.inmemory;

import de.team7.data.inmemory.repository.config.InMemoryRepositoryConfigExtension;
import de.team7.data.inmemory.repository.support.InMemoryRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * {@link de.team7.data.inmemory.repository.config.EnableInMemoryRepositories Auto-Configuration} for in-memory
 * repositories. Once in effect, the auto-configuration is the equivalent of enabling in-memory repositories using the
 * {@link de.team7.data.inmemory.repository.config.EnableInMemoryRepositories @EnableInMemoryRepositories} annotation.
 *
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean({InMemoryRepositoryFactoryBean.class, InMemoryRepositoryConfigExtension.class})
@Import(InMemoryRepositoriesRegistrar.class)
public class InMemoryRepositoriesAutoConfiguration {
}
