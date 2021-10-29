

package com.whaleal.icefrog.core.lang;


import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.core.util.Predicates;

/**
 * Legacy version of {@link java.util.function.Predicate java.util.function.Predicate}. Determines a
 * true or false value for a given input.
 *
 * <p>As this interface extends {@code java.util.function.Predicate}, an instance of this type may
 * be used as a {@code Predicate} directly. To use a {@code java.util.function.Predicate} where a
 * {@code com.google.icefrog.base.Predicate} is expected, use the method reference {@code
 * predicate::test}.
 *
 * <p>This interface is now a legacy type. Use {@code java.util.function.Predicate} (or the
 * appropriate primitive specialization such as {@code IntPredicate}) instead whenever possible.
 * Otherwise, at least reduce <i>explicit</i> dependencies on this type by using lambda expressions
 * or method references instead of classes, leaving your code easier to migrate in the future.
 *
 * <p>The {@link Predicates} class provides icefrog predicates and related utilities.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/FunctionalExplained">the use of {@code Predicate}</a>.
 *
 *
 * 过滤器
 * @author wh
 * @since 1.0
 */
@FunctionalInterface
public interface Predicate<T> extends java.util.function.Predicate<T> {
	/**
	 * Returns the result of applying this predicate to {@code input} (Java 8 users, see notes in the
	 * class documentation above). This method is <i>generally expected</i>, but not absolutely
	 * required, to have the following properties:
	 *
	 * <ul>
	 *   <li>Its execution does not cause any observable side effects.
	 *   <li>The computation is <i>consistent with equals</i>; that is, {@link ObjectUtil#equal
	 *       ObjectUtil.equal}{@code (a, b)} implies that {@code predicate.apply(a) ==
	 *       predicate.apply(b))}.
	 * </ul>
	 *
	 *  当转为 过滤器使用时 可以判断 是否接受对象
	 *
	 *
	 * @param input  input  检查的对象
	 * @throws NullPointerException if {@code input} is null and this predicate does not accept null
	 *                              arguments
	 * @return  boolean  是否接受对象
	 */
	boolean apply(T input);

	/**
	 * Indicates whether another object is equal to this predicate.
	 *
	 * <p>Most implementations will have no reason to override the behavior of {@link Object#equals}.
	 * However, an implementation may also choose to return {@code true} whenever {@code object} is a
	 * {@link Predicate} that it considers <i>interchangeable</i> with this one. "Interchangeable"
	 * <i>typically</i> means that {@code this.apply(t) == that.apply(t)} for all {@code t} of type
	 * {@code T}). Note that a {@code false} result from this method does not imply that the
	 * predicates are known <i>not</i> to be interchangeable.
	 */
	@Override
	boolean equals(Object object);

	@Override
	default boolean test(T input) {
		return apply(input);
	}
}
