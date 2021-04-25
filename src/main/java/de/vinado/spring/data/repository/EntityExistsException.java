package de.vinado.spring.data.repository;

import lombok.NoArgsConstructor;

/**
 * This exception is thrown in case an entity exists where none expected.
 *
 * @author Vincent Nadoll
 */
@NoArgsConstructor
public class EntityExistsException extends RuntimeException {

    public EntityExistsException(String message) {
        super(message);
    }
}
