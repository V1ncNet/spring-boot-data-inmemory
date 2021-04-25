package de.vinado.spring.data.domain;

import org.springframework.data.domain.Persistable;

import java.util.Objects;

/**
 * Convenience interface which assumes the implementing entity is new as long as {@link #getId()} returns
 * {@literal null}.
 *
 * @param <ID> the type of the identifier
 * @author Vincent Nadoll
 */
public interface Identifiable<ID> extends Persistable<ID> {

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isNew() {
        return Objects.isNull(getId());
    }
}
