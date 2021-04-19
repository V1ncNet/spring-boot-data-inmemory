package de.team7.swt.core.persitence;

import lombok.NoArgsConstructor;

/**
 * This exception is thrown in case the query expects results but none is given.
 *
 * @author Vincent Nadoll
 */
@NoArgsConstructor
public class NoResultException extends RuntimeException {

    public NoResultException(String message) {
        super(message);
    }
}
