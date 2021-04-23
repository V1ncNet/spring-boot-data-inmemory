package de.team7.data.inmemory.repository.support;

import de.team7.data.domain.Identifier;
import de.team7.data.domain.IdentifierGenerator;
import de.team7.data.domain.PrimaryKeyGenerator;
import de.team7.data.inmemory.repository.InMemoryRepository;
import de.team7.data.inmemory.repository.config.IdentifierMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * In-memory generic repository factory which instantiates {@link InMemoryRepository}s via reflection.
 *
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class InMemoryRepositoryFactory extends RepositoryFactorySupport {

    private final IdentifierMapping identifierMapping;

    @Override
    @SuppressWarnings("unchecked")
    public <T, ID> InMemoryEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return (InMemoryEntityInformation<T, ID>) InMemoryEntityInformation.getEntityInformation(domainClass);
    }

    @Override
    protected InMemoryRepository<?, ?> getTargetRepository(RepositoryInformation metadata) {
        Class<?> domainType = metadata.getDomainType();
        InMemoryEntityInformation<?, ?> entityInformation = getEntityInformation(domainType);

        PrimaryKeyGenerator<?> generator;
        if (null != identifierMapping) {
            generator = identifierMapping.get().get(entityInformation.getIdType());
        } else {
            generator = new DefaultConstructorIdentifierGenerator<>(domainType);
        }

        Object repository = getTargetRepositoryViaReflection(metadata, generator);
        Assert.isInstanceOf(InMemoryRepository.class, repository);

        return (InMemoryRepository<?, ?>) repository;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleInMemoryRepository.class;
    }

    @RequiredArgsConstructor
    private class DefaultConstructorIdentifierGenerator<ID extends Identifier> implements IdentifierGenerator<ID> {

        private final Class<?> domainEntity;

        @Override
        public ID createIdentifier() {
            EntityInformation<?, ID> entityInformation = getEntityInformation(domainEntity);
            return getEntityIdentifierViaReflection(entityInformation.getIdType())
                .orElseThrow(() -> new IllegalStateException(
                    "No suitable constructor found to instantiate a new identifier generator"
                ));
        }

        @SuppressWarnings("unchecked")
        private Optional<ID> getEntityIdentifierViaReflection(Class<?> idType) {
            return ReflectionUtils.findConstructor(idType)
                .map(constructor -> (ID) BeanUtils.instantiateClass(constructor));
        }
    }
}
