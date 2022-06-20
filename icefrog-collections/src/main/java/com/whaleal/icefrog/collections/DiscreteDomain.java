package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.NumberUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.NoSuchElementException;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;
import static com.whaleal.icefrog.core.lang.Precondition.checkNonnegative;

/**
 * A descriptor for a <i>discrete</i> {@code Comparable} domain such as all {@link Integer}
 * instances. A discrete domain is one that supports the three basic operations: {@link #next},
 * {@link #previous} and {@link #distance}, according to their specifications. The methods {@link
 * #minValue} and {@link #maxValue} should also be overridden for bounded types.
 *
 * <p>A discrete domain always represents the <i>entire</i> set of values of its type; it cannot
 * represent partial domains such as "prime integers" or "strings of length 5."
 *
 * <p>See the Guava User Guide section on <a href=
 * "https://github.com/google/guava/wiki/RangesExplained#discrete-domains"> {@code
 * DiscreteDomain}</a>.
 */


public abstract class DiscreteDomain<C extends Comparable> {

    final boolean supportsFastOffset;

    /**
     * Constructor for use by subclasses.
     */
    protected DiscreteDomain() {
        this(false);
    }

    /**
     * Private constructor for built-in DiscreteDomains supporting fast offset.
     */
    private DiscreteDomain( boolean supportsFastOffset ) {
        this.supportsFastOffset = supportsFastOffset;
    }

    /**
     * Returns the discrete domain for values of type {@code Integer}.
     */
    public static DiscreteDomain<Integer> integers() {
        return IntegerDomain.INSTANCE;
    }

    /**
     * Returns the discrete domain for values of type {@code Long}.
     */
    public static DiscreteDomain<Long> longs() {
        return LongDomain.INSTANCE;
    }

    /**
     * Returns the discrete domain for values of type {@code BigInteger}.
     */
    public static DiscreteDomain<BigInteger> bigIntegers() {
        return BigIntegerDomain.INSTANCE;
    }

    /**
     * Returns, conceptually, "origin + distance", or equivalently, the result of calling {@link
     * #next} on {@code origin} {@code distance} times.
     */
    C offset( C origin, long distance ) {
        C current = origin;
        checkNonnegative(distance, "distance");
        for (long i = 0; i < distance; i++) {
            current = next(current);
            if (current == null) {
                throw new IllegalArgumentException(
                        "overflowed computing offset(" + origin + ", " + distance + ")");
            }
        }
        return current;
    }

    /**
     * Returns the unique least value of type {@code C} that is greater than {@code value}, or {@code
     * null} if none exists. Inverse operation to {@link #previous}.
     *
     * @param value any value of type {@code C}
     * @return the least value greater than {@code value}, or {@code null} if {@code value} is {@code
     * maxValue()}
     */
    @CheckForNull
    public abstract C next( C value );

    /**
     * Returns the unique greatest value of type {@code C} that is less than {@code value}, or {@code
     * null} if none exists. Inverse operation to {@link #next}.
     *
     * @param value any value of type {@code C}
     * @return the greatest value less than {@code value}, or {@code null} if {@code value} is {@code
     * minValue()}
     */
    @CheckForNull
    public abstract C previous( C value );

    /**
     * Returns a signed value indicating how many nested invocations of {@link #next} (if positive) or
     * {@link #previous} (if negative) are needed to reach {@code end} starting from {@code start}.
     * For example, if {@code end = next(next(next(start)))}, then {@code distance(start, end) == 3}
     * and {@code distance(end, start) == -3}. As well, {@code distance(a, a)} is always zero.
     *
     * <p>Note that this function is necessarily well-defined for any discrete type.
     *
     * @return the distance as described above, or {@link Long#MIN_VALUE} or {@link Long#MAX_VALUE} if
     * the distance is too small or too large, respectively.
     */
    public abstract long distance( C start, C end );

    /**
     * Returns the minimum value of type {@code C}, if it has one. The minimum value is the unique
     * value for which {@link Comparable#compareTo(Object)} never returns a positive value for any
     * input of type {@code C}.
     *
     * <p>The default implementation throws {@code NoSuchElementException}.
     *
     * @return the minimum value of type {@code C}; never null
     * @throws NoSuchElementException if the type has no (practical) minimum value; for example,
     *                                {@link BigInteger}
     */

    public C minValue() {
        throw new NoSuchElementException();
    }

    /**
     * Returns the maximum value of type {@code C}, if it has one. The maximum value is the unique
     * value for which {@link Comparable#compareTo(Object)} never returns a negative value for any
     * input of type {@code C}.
     *
     * <p>The default implementation throws {@code NoSuchElementException}.
     *
     * @return the maximum value of type {@code C}; never null
     * @throws NoSuchElementException if the type has no (practical) maximum value; for example,
     *                                {@link BigInteger}
     */

    public C maxValue() {
        throw new NoSuchElementException();
    }

    private static final class IntegerDomain extends DiscreteDomain<Integer> implements Serializable {
        private static final IntegerDomain INSTANCE = new IntegerDomain();
        private static final long serialVersionUID = 0;

        IntegerDomain() {
            super(true);
        }

        @Override
        @CheckForNull
        public Integer next( Integer value ) {
            int i = value;
            return (i == Integer.MAX_VALUE) ? null : i + 1;
        }

        @Override
        @CheckForNull
        public Integer previous( Integer value ) {
            int i = value;
            return (i == Integer.MIN_VALUE) ? null : i - 1;
        }

        @Override
        Integer offset( Integer origin, long distance ) {
            checkNonnegative(distance, "distance");
            return NumberUtil.checkedCast(origin.longValue() + distance);
        }

        @Override
        public long distance( Integer start, Integer end ) {
            return (long) end - start;
        }

        @Override
        public Integer minValue() {
            return Integer.MIN_VALUE;
        }

        @Override
        public Integer maxValue() {
            return Integer.MAX_VALUE;
        }

        private Object readResolve() {
            return INSTANCE;
        }

        @Override
        public String toString() {
            return "DiscreteDomain.integers()";
        }
    }

    private static final class LongDomain extends DiscreteDomain<Long> implements Serializable {
        private static final LongDomain INSTANCE = new LongDomain();
        private static final long serialVersionUID = 0;

        LongDomain() {
            super(true);
        }

        @Override
        @CheckForNull
        public Long next( Long value ) {
            long l = value;
            return (l == Long.MAX_VALUE) ? null : l + 1;
        }

        @Override
        @CheckForNull
        public Long previous( Long value ) {
            long l = value;
            return (l == Long.MIN_VALUE) ? null : l - 1;
        }

        @Override
        Long offset( Long origin, long distance ) {
            checkNonnegative(distance, "distance");
            long result = origin + distance;
            if (result < 0) {
                checkArgument(origin < 0, "overflow");
            }
            return result;
        }

        @Override
        public long distance( Long start, Long end ) {
            long result = end - start;
            if (end > start && result < 0) { // overflow
                return Long.MAX_VALUE;
            }
            if (end < start && result > 0) { // underflow
                return Long.MIN_VALUE;
            }
            return result;
        }

        @Override
        public Long minValue() {
            return Long.MIN_VALUE;
        }

        @Override
        public Long maxValue() {
            return Long.MAX_VALUE;
        }

        private Object readResolve() {
            return INSTANCE;
        }

        @Override
        public String toString() {
            return "DiscreteDomain.longs()";
        }
    }

    private static final class BigIntegerDomain extends DiscreteDomain<BigInteger>
            implements Serializable {
        private static final BigIntegerDomain INSTANCE = new BigIntegerDomain();
        private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
        private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
        private static final long serialVersionUID = 0;

        BigIntegerDomain() {
            super(true);
        }

        @Override
        public BigInteger next( BigInteger value ) {
            return value.add(BigInteger.ONE);
        }

        @Override
        public BigInteger previous( BigInteger value ) {
            return value.subtract(BigInteger.ONE);
        }

        @Override
        BigInteger offset( BigInteger origin, long distance ) {
            checkNonnegative(distance, "distance");
            return origin.add(BigInteger.valueOf(distance));
        }

        @Override
        public long distance( BigInteger start, BigInteger end ) {
            return end.subtract(start).max(MIN_LONG).min(MAX_LONG).longValue();
        }

        private Object readResolve() {
            return INSTANCE;
        }

        @Override
        public String toString() {
            return "DiscreteDomain.bigIntegers()";
        }
    }
}
