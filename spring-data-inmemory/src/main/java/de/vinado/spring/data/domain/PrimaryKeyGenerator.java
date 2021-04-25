package de.vinado.spring.data.domain;

import org.springframework.lang.Nullable;

/**
 * A generator of identifiers of the given type.
 *
 * @param <ID> the type of the identifier
 * @author Vincent Nadoll
 */
@FunctionalInterface
public interface PrimaryKeyGenerator<ID> {

    /**
     * Gives the next primary key given the previous key value.
     *
     * @param previous my be {@literal null}
     * @return the next identifier
     */
    ID next(@Nullable ID previous);

    /**
     * Returns a key generator that always returns its input argument.
     *
     * @param <ID> the type of the input and output objects to the function
     * @return a key generator which always returns its input argument
     */
    static <ID> PrimaryKeyGenerator<ID> identity() {
        return previous -> previous;
    }
}
