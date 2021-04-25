package de.vinado.spring.data.domain;

/**
 * A primary key generator which increments the previous long value, but starts at 1 in case the previous value is
 * {@literal null}.
 *
 * @author Vincent Nadoll
 */
public class LongPrimaryKeyGenerator implements PrimaryKeyGenerator<Long> {

    @Override
    public Long next(Long previous) {
        if (null == previous || previous < 1L) {
            return 1L;
        }
        return ++previous;
    }
}
