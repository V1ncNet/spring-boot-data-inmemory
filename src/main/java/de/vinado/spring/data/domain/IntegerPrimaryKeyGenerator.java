package de.vinado.spring.data.domain;

/**
 * A primary key generator which increments the previous integer value, but starts at 1 in case the previous value is
 * {@literal null}.
 *
 * @author Vincent Nadoll
 */
public class IntegerPrimaryKeyGenerator implements PrimaryKeyGenerator<Integer> {

    @Override
    public Integer next(Integer previous) {
        if (null == previous || previous < 1) {
            return 1;
        }
        return ++previous;
    }
}
