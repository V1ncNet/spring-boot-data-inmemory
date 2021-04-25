package de.vinado.spring.data.inmemory.repository;

import de.vinado.spring.data.repository.StreamableRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Simple flag repository interface.
 *
 * @author Vincent Nadoll
 */
@NoRepositoryBean
public interface InMemoryRepository<T, ID> extends StreamableRepository<T, ID> {
}
