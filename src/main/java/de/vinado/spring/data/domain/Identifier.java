package de.vinado.spring.data.domain;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A value object wrapping a {@literal UUID} to be used to type save identify entities.
 *
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Identifier implements Comparable<Identifier>, Serializable {

    @NonNull
    private final UUID id;

    protected Identifier() {
        this.id = UUID.randomUUID();
    }

    public UUID value() {
        return id;
    }

    @Override
    public int compareTo(Identifier o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Identifier that = (Identifier) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toString(id);
    }
}
