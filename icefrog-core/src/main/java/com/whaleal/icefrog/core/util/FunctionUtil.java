package com.whaleal.icefrog.core.util;


import com.whaleal.icefrog.core.lang.Predicate;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * Static utility methods pertaining to {@code com.whaleal.icefrog.lang.Function} instances; see that
 * class for information about migrating to {@code java.util.Function}.
 *
 * <p>All methods return serializable Functions as long as they're given serializable parameters.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/FunctionalExplained">the use of {@code Function}</a>.
 */

public final class FunctionUtil {
    private FunctionUtil() {
    }

    /**
     * A Function equivalent to the method reference {@code Object::toString}, for users not yet using
     * Java 8. The Function simply invokes {@code toString} on its argument and returns the result. It
     * throws a {@link NullPointerException} on null input.
     *
     * <p><b>Warning:</b> The returned Function may not be <i>consistent with equals</i> (as
     * documented at {@link Function#apply}). For example, this Function yields different results for
     * the two equal instances {@code ImmutableSet.of(1, 2)} and {@code ImmutableSet.of(2, 1)}.
     *
     * <p><b>Warning:</b> as with all Function types in this package, avoid depending on the specific
     * {@code equals}, {@code hashCode} or {@code toString} behavior of the returned Function. A
     * future migration to {@code java.util.Function} will not preserve this behavior.
     *
     * <p><b>For Java 8 users:</b> use the method reference {@code Object::toString} instead. In the
     * future, when this class requires Java 8, this method will be deprecated. See {@link Function}
     * for more important information about the Java 8 transition.
     *
     * @return ToStringFunction 实例
     */
    public static Function<Object, String> toStringFunction() {
        return ToStringFunction.INSTANCE;
    }

    /**
     * Returns the identity Function.
     *
     * @param <E> 泛型参数
     * @return 返回值
     */
    // implementation is "fully variant"; E has become a "pass-through" type
    @SuppressWarnings("unchecked")
    public static <E extends Object> Function<E, E> identity() {
        return (Function<E, E>) IdentityFunction.INSTANCE;
    }

    /**
     * Returns a Function which performs a map lookup. The returned Function throws an {@link
     * IllegalArgumentException} if given a key that does not exist in the map. See also {@link
     * #forMap(Map, Object)}, which returns a default value in this case.
     *
     * <p>Note: if {@code map} , you
     * can use { MapUtil.asConverter} instead to get a
     * Function that also supports reverse conversion.
     *
     * <p><b>Java 8 users:</b> if you are okay with {@code null} being returned for an unrecognized
     * key (instead of an exception being thrown), you can use the method reference {@code map::get}
     * instead.
     *
     * @param map 入参
     * @param <K> 键
     * @param <V> 值
     * @return FunctionForMapNoDefault
     */
    public static <K extends Object, V extends Object> Function<K, V> forMap(
            Map<K, V> map ) {
        return new FunctionForMapNoDefault<>(map);
    }

    /**
     * Returns a Function which performs a map lookup with a default value. The Function created by
     * this method returns {@code defaultValue} for all inputs that do not belong to the map's key
     * set. See also {@link #forMap(Map)}, which throws an exception in this case.
     *
     * <p><b>Java 8 users:</b> you can just write the lambda expression {@code k ->
     * map.getOrDefault(k, defaultValue)} instead.
     *
     * @param map          source map that determines the Function behavior
     * @param defaultValue the value to return for inputs that aren't map keys
     * @param <K>          k
     * @param <V>          v
     * @return Function that returns {@code map.get(a)} when {@code a} is a key, or {@code
     * defaultValue} otherwise
     */
    public static <K extends Object, V extends Object> Function<K, V> forMap(
            Map<K, ? extends V> map, V defaultValue ) {
        return new ForMapWithDefault<>(map, defaultValue);
    }

    /**
     * Returns the composition of two Functions. For {@code f: A->B} and {@code g: B->C}, composition
     * is defined as the Function h such that {@code h(a) == g(f(a))} for each {@code a}.
     *
     * <p><b>Java 8 users:</b> use {@code g.compose(f)} or (probably clearer) {@code f.andThen(g)}
     * instead.
     *
     * @param g   the second Function to apply
     * @param f   the first Function to apply
     * @param <A> A
     * @param <B> B
     * @param <C> C
     * @return the composition of {@code f} and {@code g}
     * @see <a href="//en.wikipedia.org/wiki/Function_composition">Function composition</a>
     */
    public static <A extends Object, B extends Object, C extends Object>
    Function<A, C> compose( Function<B, C> g, Function<A, ? extends B> f ) {
        return new FunctionComposition<>(g, f);
    }

    /**
     * Creates a Function that returns the same boolean output as the given predicate for all inputs.
     *
     * <p>The returned Function is <i>consistent with equals</i> (as documented at {@link
     * Function#apply}) if and only if {@code predicate} is itself consistent with equals.
     *
     * <p><b>Java 8 users:</b> use the method reference {@code predicate::test} instead.
     *
     * @param predicate predicate
     * @param <T>       T
     * @return function
     */
    public static <T extends Object> Function<T, Boolean> forPredicate(
            Predicate<T> predicate ) {
        return new PredicateFunction<T>(predicate);
    }

    /**
     * Returns a Function that ignores its input and always returns {@code value}.
     *
     * <p><b>Java 8 users:</b> use the lambda expression {@code o -> value} instead.
     *
     * @param value the constant value for the Function to return
     * @param <E>   E
     * @return a Function that always returns {@code value}
     */
    public static <E extends Object> Function<Object, E> constant(
            E value ) {
        return new ConstantFunction<>(value);
    }

    /**
     * Returns a Function that ignores its input and returns the result of {@code supplier.get()}.
     *
     * <p><b>Java 8 users:</b> use the lambda expression {@code o -> supplier.get()} instead.
     *
     * @param supplier supplier
     * @param <T>      T
     * @param <F>      F
     * @return function
     */
    public static <F extends Object, T extends Object> Function<F, T> forSupplier(
            Supplier<T> supplier ) {
        return new SupplierFunction<>(supplier);
    }

    // enum singleton pattern
    private enum ToStringFunction implements Function<Object, String> {
        INSTANCE;

        @Override
        public String apply( Object o ) {
            checkNotNull(o); // eager for GWT.
            return o.toString();
        }

        @Override
        public String toString() {
            return "Functions.toStringFunction()";
        }
    }

    // enum singleton pattern
    private enum IdentityFunction implements Function<Object, Object> {
        INSTANCE;

        @Override

        public Object apply( Object o ) {
            return o;
        }

        @Override
        public String toString() {
            return "Functions.identity()";
        }
    }

    private static class FunctionForMapNoDefault<
            K extends Object, V extends Object>
            implements Function<K, V>, Serializable {
        private static final long serialVersionUID = 0;
        final Map<K, V> map;

        FunctionForMapNoDefault( Map<K, V> map ) {
            this.map = checkNotNull(map);
        }

        @Override
        public V apply( K key ) {
            V result = map.get(key);
            checkArgument(result != null || map.containsKey(key), "Key '%s' not present in map", key);
            // The unchecked cast is safe because of the containsKey check.
            return (result);
        }

        @Override
        public boolean equals( Object o ) {
            if (o instanceof FunctionForMapNoDefault) {
                FunctionForMapNoDefault<?, ?> that = (FunctionForMapNoDefault<?, ?>) o;
                return map.equals(that.map);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return map.hashCode();
        }

        @Override
        public String toString() {
            return "Functions.forMap(" + map + ")";
        }
    }

    private static class ForMapWithDefault<K extends Object, V extends Object>
            implements Function<K, V>, Serializable {
        private static final long serialVersionUID = 0;
        final Map<K, ? extends V> map;
        final V defaultValue;

        ForMapWithDefault( Map<K, ? extends V> map, V defaultValue ) {
            this.map = checkNotNull(map);
            this.defaultValue = defaultValue;
        }

        @Override

        public V apply( K key ) {
            V result = map.get(key);
            // The unchecked cast is safe because of the containsKey check.
            return (result != null || map.containsKey(key))
                    ? (result)
                    : defaultValue;
        }

        @Override
        public boolean equals( Object o ) {
            if (o instanceof ForMapWithDefault) {
                ForMapWithDefault<?, ?> that = (ForMapWithDefault<?, ?>) o;
                return map.equals(that.map) && ObjectUtil.equal(defaultValue, that.defaultValue);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return ObjectUtil.hashCode(map, defaultValue);
        }

        @Override
        public String toString() {
            // TODO(cpovirk): maybe remove "defaultValue=" to make this look like the method call does
            return "Functions.forMap(" + map + ", defaultValue=" + defaultValue + ")";
        }
    }

    private static class FunctionComposition<
            A extends Object, B extends Object, C extends Object>
            implements Function<A, C>, Serializable {
        private static final long serialVersionUID = 0;
        private final Function<B, C> g;
        private final Function<A, ? extends B> f;

        public FunctionComposition( Function<B, C> g, Function<A, ? extends B> f ) {
            this.g = checkNotNull(g);
            this.f = checkNotNull(f);
        }

        @Override

        public C apply( A a ) {
            try {
                return g.apply(f.apply(a));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj instanceof FunctionComposition) {
                FunctionComposition<?, ?, ?> that = (FunctionComposition<?, ?, ?>) obj;
                return f.equals(that.f) && g.equals(that.g);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return f.hashCode() ^ g.hashCode();
        }

        @Override
        public String toString() {
            // TODO(cpovirk): maybe make this look like the method call does ("Functions.compose(...)")
            return g + "(" + f + ")";
        }
    }

    /**
     * @see FunctionUtil#forPredicate
     */
    private static class PredicateFunction<T extends Object>
            implements Function<T, Boolean>, Serializable {
        private static final long serialVersionUID = 0;
        private final Predicate<T> predicate;

        private PredicateFunction( Predicate<T> predicate ) {
            this.predicate = checkNotNull(predicate);
        }

        @Override
        public Boolean apply( T t ) {
            return predicate.apply(t);
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj instanceof PredicateFunction) {
                PredicateFunction<?> that = (PredicateFunction<?>) obj;
                return predicate.equals(that.predicate);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return predicate.hashCode();
        }

        @Override
        public String toString() {
            return "Functions.forPredicate(" + predicate + ")";
        }
    }

    private static class ConstantFunction<E extends Object>
            implements Function<Object, E>, Serializable {
        private static final long serialVersionUID = 0;
        private final E value;

        public ConstantFunction( E value ) {
            this.value = value;
        }

        @Override

        public E apply( Object from ) {
            return value;
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj instanceof ConstantFunction) {
                ConstantFunction<?> that = (ConstantFunction<?>) obj;
                return ObjectUtil.equal(value, that.value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (value == null) ? 0 : value.hashCode();
        }

        @Override
        public String toString() {
            return "Functions.constant(" + value + ")";
        }
    }

    /**
     * @see FunctionUtil#forSupplier
     */
    private static class SupplierFunction<F extends Object, T extends Object>
            implements Function<F, T>, Serializable {

        private static final long serialVersionUID = 0;
        private final Supplier<T> supplier;

        private SupplierFunction( Supplier<T> supplier ) {
            this.supplier = checkNotNull(supplier);
        }

        @Override

        public T apply( F input ) {
            return supplier.get();
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj instanceof SupplierFunction) {
                SupplierFunction<?, ?> that = (SupplierFunction<?, ?>) obj;
                return this.supplier.equals(that.supplier);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return supplier.hashCode();
        }

        @Override
        public String toString() {
            return "Functions.forSupplier(" + supplier + ")";
        }
    }
}
