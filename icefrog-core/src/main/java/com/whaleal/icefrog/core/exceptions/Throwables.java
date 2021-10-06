
package com.whaleal.icefrog.core.exceptions;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.whaleal.icefrog.core.util.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * Static utility methods pertaining to instances of {@link Throwable}.
 *
 * <p>See the Guava User Guide entry on <a
 * href="https://github.com/google/guava/wiki/ThrowablesExplained">Throwables</a>.
 *
 * @author Kevin Bourrillion
 * @author Ben Yu
 * @since 1.0
 */


public final class Throwables {
	/**
	 * SharedSecrets class name to load using reflection
	 */
// not used by GWT emulation

	static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
	/**
	 * JavaLangAccess class name to load using reflection
	 */
// not used by GWT emulation
	private static final String JAVA_LANG_ACCESS_CLASSNAME = "sun.misc.JavaLangAccess";
	/**
	 * Access to some fancy internal JVM internals.
	 */
// java.lang.reflect

	private static final Object jla = getJLA();
	/**
	 * The "getStackTraceElementMethod" method, only available on some JDKs so we use reflection to
	 * find it when available. When this is null, use the slow way.
	 */
// java.lang.reflect

	private static final Method getStackTraceElementMethod = (jla == null) ? null : getGetMethod();
	/**
	 * The "getStackTraceDepth" method, only available on some JDKs so we use reflection to find it
	 * when available. When this is null, use the slow way.
	 */
// java.lang.reflect

	private static final Method getStackTraceDepthMethod = (jla == null) ? null : getSizeMethod(jla);

	private Throwables() {
	}

	/**
	 * Throws {@code throwable} if it is an instance of {@code declaredType}. Example usage:
	 *
	 * <pre>
	 * for (Foo foo : foos) {
	 *   try {
	 *     foo.bar();
	 *   } catch (BarException | RuntimeException | Error t) {
	 *     failure = t;
	 *   }
	 * }
	 * if (failure != null) {
	 *   throwIfInstanceOf(failure, BarException.class);
	 *   throwIfUnchecked(failure);
	 *   throw new AssertionError(failure);
	 * }
	 * </pre>
	 * @param declaredType type
	 * @param throwable throwable
	 * @param <X> x
	 * @throws X x
	 *
	 *
	 *
	 */
// Class.cast, Class.isInstance
	public static <X extends Throwable> void throwIfInstanceOf(
			Throwable throwable, Class<X> declaredType) throws X {
		checkNotNull(throwable);
		if (declaredType.isInstance(throwable)) {
			throw declaredType.cast(throwable);
		}
	}



	/**
	 * Throws {@code throwable} if it is a {@link RuntimeException} or {@link Error}. Example usage:
	 *
	 * <pre>
	 * for (Foo foo : foos) {
	 *   try {
	 *     foo.bar();
	 *   } catch (RuntimeException | Error t) {
	 *     failure = t;
	 *   }
	 * }
	 * if (failure != null) {
	 *   throwIfUnchecked(failure);
	 *   throw new AssertionError(failure);
	 * }
	 * </pre>
	 *
	 * @param throwable throwable
	 *
	 */
	public static void throwIfUnchecked(Throwable throwable) {
		checkNotNull(throwable);
		if (throwable instanceof RuntimeException) {
			throw (RuntimeException) throwable;
		}
		if (throwable instanceof Error) {
			throw (Error) throwable;
		}
	}


	/**
	 * Propagates {@code throwable} exactly as-is, if and only if it is an instance of {@link
	 * RuntimeException}, {@link Error}, or {@code declaredType}. Example usage:
	 *
	 * <pre>
	 * try {
	 *   someMethodThatCouldThrowAnything();
	 * } catch (IKnowWhatToDoWithThisException e) {
	 *   handle(e);
	 * } catch (Throwable t) {
	 *   Throwables.propagateIfPossible(t, OtherException.class);
	 *   throw new RuntimeException("unexpected", t);
	 * }
	 * </pre>
	 *
	 * @param throwable    the Throwable to possibly propagate
	 * @param declaredType the single checked exception type declared by the calling method
	 * @param <X> x x
	 * @throws X   x
	 */
// propagateIfInstanceOf
	public static <X extends Throwable> void propagateIfPossible(
			Throwable throwable, Class<X> declaredType) throws X {
		throwIfInstanceOf(throwable, declaredType);
		throwIfUnchecked(throwable);
	}

	/**
	 * Propagates {@code throwable} exactly as-is, if and only if it is an instance of {@link
	 * RuntimeException}, {@link Error}, {@code declaredType1}, or {@code declaredType2}. In the
	 * unlikely case that you have three or more declared checked exception types, you can handle them
	 * all by invoking these methods repeatedly. See usage example in {@link
	 * #propagateIfPossible(Throwable, Class)}.
	 *
	 * @param throwable     the Throwable to possibly propagate
	 * @param declaredType1 any checked exception type declared by the calling method
	 * @param declaredType2 any other checked exception type declared by the calling method
	 * @param <X1> x1
	 * @param <X2> x2
	 * @throws X1 x1
	 * @throws X2 x2
	 */
// propagateIfInstanceOf
	public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(
			Throwable throwable, Class<X1> declaredType1, Class<X2> declaredType2)
			throws X1, X2 {
		checkNotNull(declaredType2);
		throwIfInstanceOf(throwable, declaredType1);
		propagateIfPossible(throwable, declaredType2);
	}



	/**
	 * Returns the innermost cause of {@code throwable}. The first throwable in a chain provides
	 * context from when the error or exception was initially detected. Example usage:
	 *
	 * <pre>
	 * assertEquals("Unable to assign a customer id", Throwables.getRootCause(e).getMessage());
	 * </pre>
	 *
	 * @param throwable Throwable
	 * @throws IllegalArgumentException if there is a loop in the causal chain
	 * @return Throwable
	 */
	public static Throwable getRootCause(Throwable throwable) {
		// Keep a second pointer that slowly walks the causal chain. If the fast pointer ever catches
		// the slower pointer, then there's a loop.
		Throwable slowPointer = throwable;
		boolean advanceSlowPointer = false;

		Throwable cause;
		while ((cause = throwable.getCause()) != null) {
			throwable = cause;

			if (throwable == slowPointer) {
				throw new IllegalArgumentException("Loop in causal chain detected.", throwable);
			}
			if (advanceSlowPointer) {
				slowPointer = slowPointer.getCause();
			}
			advanceSlowPointer = !advanceSlowPointer; // only advance every other iteration
		}
		return throwable;
	}

	/**
	 * Gets a {@code Throwable} cause chain as a list. The first entry in the list will be {@code
	 * throwable} followed by its cause hierarchy. Note that this is a snapshot of the cause chain and
	 * will not reflect any subsequent changes to the cause chain.
	 *
	 * <p>Here's an example of how it can be used to find specific types of exceptions in the cause
	 * chain:
	 *
	 * <pre>
	 * Iterables.filter(Throwables.getCausalChain(e), IOException.class));
	 * </pre>
	 *
	 * @param throwable the non-null {@code Throwable} to extract causes from
	 * @return an unmodifiable list containing the cause chain starting with {@code throwable}
	 * @throws IllegalArgumentException if there is a loop in the causal chain
	 */
	// TODO(kevinb): decide best return type
	public static List<Throwable> getCausalChain(Throwable throwable) {
		checkNotNull(throwable);
		List<Throwable> causes = new ArrayList<>(4);
		causes.add(throwable);

		// Keep a second pointer that slowly walks the causal chain. If the fast pointer ever catches
		// the slower pointer, then there's a loop.
		Throwable slowPointer = throwable;
		boolean advanceSlowPointer = false;

		Throwable cause;
		while ((cause = throwable.getCause()) != null) {
			throwable = cause;
			causes.add(throwable);

			if (throwable == slowPointer) {
				throw new IllegalArgumentException("Loop in causal chain detected.", throwable);
			}
			if (advanceSlowPointer) {
				slowPointer = slowPointer.getCause();
			}
			advanceSlowPointer = !advanceSlowPointer; // only advance every other iteration
		}
		return Collections.unmodifiableList(causes);
	}

	/**
	 * Returns {@code throwable}'s cause, cast to {@code expectedCauseType}.
	 *
	 * <p>Prefer this method instead of manually casting an exception's cause. For example, {@code
	 * (IOException) e.getCause()} throws a {@link ClassCastException} that discards the original
	 * exception {@code e} if the cause is not an {@link IOException}, but {@code
	 * Throwables.getCauseAs(e, IOException.class)} keeps {@code e} as the {@link
	 * ClassCastException}'s cause.
	 *
	 * @param throwable throwable
	 * @param expectedCauseType type
	 * @param <X> x
	 * @return x
	 * @throws ClassCastException if the cause cannot be cast to the expected type. The {@code
	 *                            ClassCastException}'s cause is {@code throwable}.
	 */

// Class.cast(Object)
	public static <X extends Throwable> X getCauseAs(
			Throwable throwable, Class<X> expectedCauseType) {
		try {
			return expectedCauseType.cast(throwable.getCause());
		} catch (ClassCastException e) {
			e.initCause(throwable);
			throw e;
		}
	}



	/**
	 * Returns the stack trace of {@code throwable}, possibly providing slower iteration over the full
	 * trace but faster iteration over parts of the trace. Here, "slower" and "faster" are defined in
	 * comparison to the normal way to access the stack trace, {@link Throwable#getStackTrace()
	 * throwable.getStackTrace()}. Note, however, that this method's special implementation is not
	 * available for all platforms and configurations. If that implementation is unavailable, this
	 * method falls back to {@code getStackTrace}. Callers that require the special implementation can
	 * check its availability with {@link #lazyStackTraceIsLazy()}.
	 *
	 * <p>The expected (but not guaranteed) performance of the special implementation differs from
	 * {@code getStackTrace} in one main way: The {@code lazyStackTrace} call itself returns quickly
	 * by delaying the per-stack-frame work until each element is accessed. Roughly speaking:
	 *
	 * <ul>
	 *   <li>{@code getStackTrace} takes {@code stackSize} time to return but then negligible time to
	 *       retrieve each element of the returned list.
	 *   <li>{@code lazyStackTrace} takes negligible time to return but then {@code 1/stackSize} time
	 *       to retrieve each element of the returned list (probably slightly more than {@code
	 *       1/stackSize}).
	 * </ul>
	 *
	 * <p>Note: The special implementation does not respect calls to {@link Throwable#setStackTrace
	 * throwable.setStackTrace}. Instead, it always reflects the original stack trace from the
	 * exception's creation.
	 *
	 *
	 * @param throwable throwable
	 * @return list
	 *
	 */
	// TODO(cpovirk): Say something about the possibility that List access could fail at runtime?

// lazyStackTraceIsLazy, jlaStackTrace
	// TODO(cpovirk): Consider making this available under GWT (slow implementation only).
	public static List<StackTraceElement> lazyStackTrace(Throwable throwable) {
		return lazyStackTraceIsLazy()
				? jlaStackTrace(throwable)
				: unmodifiableList(asList(throwable.getStackTrace()));
	}

	/**
	 * Returns whether {@link #lazyStackTrace} will use the special implementation described in its
	 * documentation.
	 *
	 * @return boolean
	 */

// getStackTraceElementMethod
	public static boolean lazyStackTraceIsLazy() {
		return getStackTraceElementMethod != null && getStackTraceDepthMethod != null;
	}

	/**
	 *
	 * @param t t
	 * @return List
	 */
	// invokeAccessibleNonThrowingMethod
	private static List<StackTraceElement> jlaStackTrace(final Throwable t) {
		checkNotNull(t);
		/*
		 * TODO(cpovirk): Consider optimizing iterator() to catch IOOBE instead of doing bounds checks.
		 *
		 * TODO(cpovirk): Consider the UnsignedBytes pattern if it performs faster and doesn't cause
		 * AOSP grief.
		 */
		return new AbstractList<StackTraceElement>() {
			/*
			 * The following requireNonNull calls are safe because we use jlaStackTrace() only if
			 * lazyStackTraceIsLazy() returns true.
			 */
			@Override
			public StackTraceElement get(int n) {
				return (StackTraceElement)
						invokeAccessibleNonThrowingMethod(
								requireNonNull(getStackTraceElementMethod), requireNonNull(jla), t, n);
			}

			@Override
			public int size() {
				return (Integer)
						invokeAccessibleNonThrowingMethod(
								requireNonNull(getStackTraceDepthMethod), requireNonNull(jla), t);
			}
		};
	}

	// java.lang.reflect
	private static Object invokeAccessibleNonThrowingMethod(
			Method method, Object receiver, Object... params) {
		try {
			return method.invoke(receiver, params);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			 throwIfUnchecked(e.getCause());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the JavaLangAccess class that is present in all Sun JDKs. It is not allowed in
	 * AppEngine, and not present in non-Sun JDKs.
	 * @return Object
	 */
// java.lang.reflect
	private static Object getJLA() {
		try {
			/*
			 * We load sun.misc.* classes using reflection since Android doesn't support these classes and
			 * would result in compilation failure if we directly refer to these classes.
			 */
			Class<?> sharedSecrets = Class.forName(SHARED_SECRETS_CLASSNAME, false, null);
			Method langAccess = sharedSecrets.getMethod("getJavaLangAccess");
			return langAccess.invoke(null);
		} catch (ThreadDeath death) {
			throw death;
		} catch (Throwable t) {
			/*
			 * This is not one of AppEngine's allowed classes, so even in Sun JDKs, this can fail with
			 * a NoClassDefFoundError. Other apps might deny access to sun.misc packages.
			 */
			return null;
		}
	}

	/**
	 * Returns the Method that can be used to resolve an individual StackTraceElement, or null if that
	 * method cannot be found (it is only to be found in fairly recent JDKs).
	 */
// java.lang.reflect
	private static Method getGetMethod() {
		return getJlaMethod("getStackTraceElement", Throwable.class, int.class);
	}

	/**
	 * Returns the Method that can be used to return the size of a stack, or null if that method
	 * cannot be found (it is only to be found in fairly recent JDKs). Tries to test method {@link
	 * sun.misc.JavaLangAccess#getStackTraceDepth(Throwable)} getStackTraceDepth} prior to return it
	 * (might fail some JDKs).
	 *
	 * <p>See <a href="https://github.com/google/guava/issues/2887">Throwables#lazyStackTrace throws
	 * UnsupportedOperationException</a>.
	 * @param jla jla
	 * @return Method
	 *
	 */
// java.lang.reflect
	private static Method getSizeMethod(Object jla) {
		try {
			Method getStackTraceDepth = getJlaMethod("getStackTraceDepth", Throwable.class);
			if (getStackTraceDepth == null) {
				return null;
			}
			getStackTraceDepth.invoke(jla, new Throwable());
			return getStackTraceDepth;
		} catch (UnsupportedOperationException | IllegalAccessException | InvocationTargetException e) {
			return null;
		}
	}

	/**
	 *
	 * @param name name
	 * @param parameterTypes types
	 * @return Method
	 * @throws ThreadDeath  ThreadDeath
	 */
	// java.lang.reflect
	private static Method getJlaMethod(String name, Class<?>... parameterTypes) throws ThreadDeath {
		try {
			return Class.forName(JAVA_LANG_ACCESS_CLASSNAME, false, null).getMethod(name, parameterTypes);
		} catch (ThreadDeath death) {
			throw death;
		} catch (Throwable t) {
			/*
			 * Either the JavaLangAccess class itself is not found, or the method is not supported on the
			 * JVM.
			 */
			return null;
		}
	}
}
