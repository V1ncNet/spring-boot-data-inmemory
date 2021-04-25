package de.vinado.spring.data.domain;

/**
 * Adapting interface which doesn't care about previous values because {@literal Identifier}s are based on universally
 * unique identifiers ({@link java.util.UUID UUIDs}).
 *
 * @author Vincent Nadoll
 */
@FunctionalInterface
public interface IdentifierGenerator<ID extends Identifier> extends PrimaryKeyGenerator<ID> {

    ID createIdentifier();

    @Override
    default ID next(ID previous) {
        return createIdentifier();
    }
}
