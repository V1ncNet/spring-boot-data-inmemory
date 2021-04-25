package de.vinado.spring.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.util.Streamable;

/**
 * An extension to Spring's {@literal CrudRepository} supporting {@literal Streamable}s instead of {@literal Iterable}s.
 * Note that the result the implementation produces has be consumed in order to execute the defined logic.
 *
 * @author Vincent Nadoll
 */
@NoRepositoryBean
public interface StreamableRepository<T, ID> extends CrudRepository<T, ID> {

    /**
     * {@inheritDoc}
     */
    @Override
    Streamable<T> findAll();

    /**
     * {@inheritDoc}
     */
    @Override
    Streamable<T> findAllById(Iterable<ID> ids);
}
