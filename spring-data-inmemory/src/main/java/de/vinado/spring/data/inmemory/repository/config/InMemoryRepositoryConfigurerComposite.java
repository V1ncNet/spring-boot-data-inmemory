package de.vinado.spring.data.inmemory.repository.config;

import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A composite configurer which does what a composite does... holding its own delegates.
 *
 * @author Vincent Nadoll
 */
class InMemoryRepositoryConfigurerComposite implements InMemoryRepositoryConfigurer {

    private final Set<InMemoryRepositoryConfigurer> delegates = new HashSet<>();

    void addInMemoryRepositoryConfigurers(Collection<InMemoryRepositoryConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            delegates.addAll(configurers);
        }
    }

    @Override
    public void addIdentifiers(IdentifierRegistry registry) {
        delegates.forEach(delegate -> delegate.addIdentifiers(registry));
    }
}
