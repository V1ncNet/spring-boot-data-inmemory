package de.vinado.spring.data.repository;

import lombok.NoArgsConstructor;

/**
 * This exception is thrown in case the repository contains entities with identical property values but the query
 * expecting them to be unique.
 *
 * @author Vincent Nadoll
 */
@NoArgsConstructor
public class NonUniqueResultException extends RuntimeException {

    public NonUniqueResultException(String message) {
        super(message);
    }
}
