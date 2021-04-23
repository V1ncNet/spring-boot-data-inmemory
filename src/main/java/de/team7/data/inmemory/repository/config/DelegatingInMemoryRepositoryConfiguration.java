package de.team7.data.inmemory.repository.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * A configuration component which performs further setup for the in-memory repositories.
 *
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
public class DelegatingInMemoryRepositoryConfiguration {

    private final InMemoryRepositoryConfigurerComposite configurers = new InMemoryRepositoryConfigurerComposite();

    @Autowired(required = false)
    public void setConfigurers(Collection<InMemoryRepositoryConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurers.addInMemoryRepositoryConfigurers(configurers);
        }
    }

    @Bean
    public IdentifierMapping identifierMapping() {
        IdentifierRegistry registry = new IdentifierRegistry();
        addIdentifiers(registry);
        return registry.get();
    }

    protected void addIdentifiers(IdentifierRegistry registry) {
        this.configurers.addIdentifiers(registry);
    }
}
