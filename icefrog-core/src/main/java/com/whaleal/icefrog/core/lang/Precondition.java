package com.whaleal.icefrog.core.lang;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.collection.CollectionUtil;
import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.ArrayUtil;
import com.whaleal.icefrog.core.util.StrUtil;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import static com.whaleal.icefrog.core.util.StrUtil.lenientFormat;

/**
 * 断言<br>
 * 断言某些对象或值是否符合规定，否则抛出异常。经常用于做变量检查
 * Static convenience methods that help a method or constructor check whether it was invoked
 * correctly (that is, whether its <i>Precondition</i> were met).
 *
 * <p>If the precondition is not met, the {@code Precondition} method throws an unchecked exception
 * of a specified type, which helps the method in which the exception was thrown communicate that
 * its caller has made a mistake. This allows constructs such as
 *
 * <pre>{@code
 * public static double sqrt(double value) {
 *   if (value < 0) {
 *     throw new IllegalArgumentException("input is negative: " + value);
 *   }
 *   // calculate square root
 * }
 * }</pre>
 *
 * <p>to be replaced with the more compact
 *
 * <pre>{@code
 * public static double sqrt(double value) {
 *   checkArgument(value >= 0, "input is negative: %s", value);
 *   // calculate square root
 * }
 * }</pre>
 *
 * <p>so that a hypothetical bad caller of this method, such as:
 *
 * <pre>{@code
 * void exampleBadCaller() {
 *   double d = sqrt(-1.0);
 * }
 * }</pre>
 *
 * <p>would be flagged as having called {@code sqrt()} with an illegal argument.
 *
 * <h3>Performance</h3>
 *
 * <p>Avoid passing message arguments that are expensive to compute; your code will always compute
 * them, even though they usually won't be needed. If you have such arguments, use the conventional
 * if/throw idiom instead.
 *
 * <p>Depending on your message arguments, memory may be allocated for boxing and varargs array
 * creation. However, the methods of this class have a large number of overloads that prevent such
 * allocations in many icefrog cases.
 *
 * <p>The message string is not formatted unless the exception will be thrown, so the cost of the
 * string formatting itself should not be a concern.
 *
 * <p>As with any performance concerns, you should consider profiling your code (in a production
 * environment if possible) before spending a lot of effort on tweaking a particular element.
 *
 * <h3>Other types of Precondition</h3>
 *
 * <p>Not every type of precondition failure is supported by these methods. Continue to throw
 * standard JDK exceptions such as {@link java.util.NoSuchElementException} or {@link
 * UnsupportedOperationException} in the situations they are intended for.
 *
 * <h3>Non-Precondition</h3>
 *
 * <p>It is of course possible to use the methods of this class to check for invalid conditions
 * which are <i>not the caller's fault</i>. Doing so is <b>not recommended</b> because it is
 * misleading to future readers of the code and of stack traces. See <a
 * href="https://github.com/google/guava/wiki/ConditionalFailuresExplained">Conditional failures
 * explained</a> in the Guava User Guide for more advice. Notably,  offers assertions
 * similar to those in this class for non-precondition checks.
 *
 * <h3>{@code java.util.Objects.requireNonNull()}</h3>
 *
 * <p>Projects which use {@code com.google.icefrog} should generally avoid the use of {@link
 * java.util.Objects#requireNonNull(Object)}. Instead, use whichever of {@link
 * #checkNotNull(Object)} or  is appropriate to the situation.
 * (The same goes for the message-accepting overloads.)
 *
 * <h3>Only {@code %s} is supported</h3>
 *
 * <p>{@code Precondition} uses {@link StrUtil#lenientFormat} to format error message template
 * StrUtil. This only supports the {@code "%s"} specifier, not the full range of {@link
 * java.util.Formatter} specifiers. However, note that if the number of arguments does not match the
 * number of occurrences of {@code "%s"} in the format string, {@code Precondition} will still
 * behave as expected, and will still include all argument values in the error message; the message
 * will simply not be formatted exactly as intended.
 *
 * <h3>More information</h3>
 *
 * <p>See the Guava User Guide on <a
 * href="https://github.com/google/guava/wiki/PreconditionExplained">using {@code
 * Precondition}</a>.
 *
 * @author Looly
 * @author wh
 */
public class Precondition {
    private Precondition() {
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出给定的异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, IllegalArgumentException::new);
     * </pre>
     *
     * @param <X>        异常类型
     * @param expression 布尔值
     * @param supplier   指定断言不通过时抛出的异常
     * @throws X if expression is {@code false}
     */
    public static <X extends Throwable> void isTrue( boolean expression, Supplier<? extends X> supplier ) throws X {
        if (false == expression) {
            throw supplier.get();
        }
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue( boolean expression, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        isTrue(expression, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression 布尔值
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue( boolean expression ) throws IllegalArgumentException {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }


    /**
     * Assert a boolean expression, throwing an {@code IllegalArgumentException}
     * if the expression evaluates to {@code false}.
     * <pre class="code">Assert.isTrue(i &gt; 0, "The value must be greater than zero");</pre>
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if {@code expression} is {@code false}
     */
    public static void isTrue( boolean expression, String message ) {
        isTrue(expression, () -> new IllegalArgumentException(StrUtil.format(message)));
    }


    /**
     * 断言是否为假，如果为 {@code true} 抛出指定类型异常<br>
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     *  Assert.isFalse(i &gt; 0, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param expression    布尔值
     * @param errorSupplier 指定断言不通过时抛出的异常
     * @throws X if expression is {@code false}
     * @since 1.0.0
     */
    public static <X extends Throwable> void isFalse( boolean expression, Supplier<X> errorSupplier ) throws X {
        if (expression) {
            throw errorSupplier.get();
        }
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isFalse(i &lt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isFalse( boolean expression, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        isFalse(expression, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isFalse(i &lt; 0);
     * </pre>
     *
     * @param expression 布尔值
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isFalse( boolean expression ) throws IllegalArgumentException {
        isFalse(expression, "[Assertion failed] - this expression must be false");
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出指定类型异常
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.isNull(value, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param object        被检查的对象
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @throws X if the object is not {@code null}
     * @since 1.0.0
     */
    public static <X extends Throwable> void isNull( Object object, Supplier<X> errorSupplier ) throws X {
        if (null != object) {
            throw errorSupplier.get();
        }
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.isNull(value, "The value must be null");
     * </pre>
     *
     * @param object           被检查的对象
     * @param errorMsgTemplate 消息模板，变量使用{}表示
     * @param params           参数列表
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull( Object object, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        isNull(object, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.isNull(value);
     * </pre>
     *
     * @param object 被检查对象
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull( Object object ) throws IllegalArgumentException {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    // ----------------------------------------------------------------------------------------------------------- Check not null

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出指定类型异常
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.notNull(clazz, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <T>           被检查对象泛型类型
     * @param <X>           异常类型
     * @param object        被检查对象
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查后的对象
     * @throws X if the object is {@code null}
     * @since 1.0.0
     */
    public static <T, X extends Throwable> T notNull( T object, Supplier<X> errorSupplier ) throws X {
        if (null == object) {
            throw errorSupplier.get();
        }
        return object;
    }

    /**
     * Throw IllegalArgumentException if the value is null.
     *
     * @param name  the parameter name
     * @param value the value that should not be null
     * @param <T>   the value type
     * @return the value
     * @throws IllegalArgumentException if value is null
     */
    public static <T> T notNull( final String name, final T value ) {
        if (value == null) {
            throw new IllegalArgumentException(name + " can not be null");
        }
        return value;
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常 Assert that an object is not {@code null} .
     *
     * <pre class="code">
     * Assert.notNull(clazz, "The class must not be null");
     * </pre>
     *
     * @param <T>              被检查对象泛型类型
     * @param object           被检查对象
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 被检查后的对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> T notNull( T object, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        return notNull(object, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * Throw IllegalArgumentException if the condition if false.
     *
     * @param name      the name of the state that is being checked
     * @param condition the condition about the parameter to check
     * @throws IllegalArgumentException if the condition is false
     */
    public static void isTrueArgument( final String name, final boolean condition ) {
        if (!condition) {
            throw new IllegalArgumentException("state should be: " + name);
        }
    }

    /**
     * Throw IllegalArgumentException if the condition if false, otherwise return the value.  This is useful when arguments must be checked
     * within an expression, as when using {@code this} to call another constructor, which must be the first line of the calling
     * constructor.
     *
     * @param <T>       the value type
     * @param name      the name of the state that is being checked
     * @param value     the value of the argument
     * @param condition the condition about the parameter to check
     * @return the value
     * @throws java.lang.IllegalArgumentException if the condition is false
     */
    public static <T> T isTrueArgument( final String name, final T value, final boolean condition ) {
        if (!condition) {
            throw new IllegalArgumentException("state should be: " + name);
        }
        return value;
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.notNull(clazz);
     * </pre>
     *
     * @param <T>    被检查对象类型
     * @param object 被检查对象
     * @return 非空对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> T notNull( T object ) throws IllegalArgumentException {
        return notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    // ----------------------------------------------------------------------------------------------------------- Check empty

    /**
     * 检查给定字符串是否为空，为空抛出自定义异常，并使用指定的函数获取错误信息返回。
     * <pre class="code">
     * Assert.notEmpty(name, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param <T>           字符串类型
     * @param text          被检查字符串
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 非空字符串
     * @throws X 被检查字符串为空抛出此异常
     * @see StrUtil#isNotEmpty(CharSequence)
     * @since 1.0.0
     */
    public static <T extends CharSequence, X extends Throwable> T notEmpty( T text, Supplier<X> errorSupplier ) throws X {
        if (StrUtil.isEmpty(text)) {
            throw errorSupplier.get();
        }
        return text;
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notEmpty(name, "Name must not be empty");
     * </pre>
     *
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空
     * @see StrUtil#isNotEmpty(CharSequence)
     */
    public static <T extends CharSequence> T notEmpty( T text, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        return notEmpty(text, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notEmpty(name);
     * </pre>
     *
     * @param <T>  字符串类型
     * @param text 被检查字符串
     * @return 被检查的字符串
     * @throws IllegalArgumentException 被检查字符串为空
     * @see StrUtil#isNotEmpty(CharSequence)
     */
    public static <T extends CharSequence> T notEmpty( T text ) throws IllegalArgumentException {
        return notEmpty(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出自定义异常。
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.notBlank(name, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>              异常类型
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgSupplier 错误抛出异常附带的消息生产接口
     * @return 非空字符串
     * @throws X 被检查字符串为空白
     * @see StrUtil#isNotBlank(CharSequence)
     */
    public static <T extends CharSequence, X extends Throwable> T notBlank( T text, Supplier<X> errorMsgSupplier ) throws X {
        if (StrUtil.isBlank(text)) {
            throw errorMsgSupplier.get();
        }
        return text;
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空白
     * @see StrUtil#isNotBlank(CharSequence)
     */
    public static <T extends CharSequence> T notBlank( T text, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        return notBlank(text, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param <T>  字符串类型
     * @param text 被检查字符串
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空白
     * @see StrUtil#isNotBlank(CharSequence)
     */
    public static <T extends CharSequence> T notBlank( T text ) throws IllegalArgumentException {
        return notBlank(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（即是否为子串）
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.notContain(name, "rod", ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return ");
     *  });
     * </pre>
     *
     * @param <T>           字符串类型
     * @param <X>           异常类型
     * @param textToSearch  被搜索的字符串
     * @param substring     被检查的子串
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查的子串
     * @throws X 非子串抛出异常
     * @see StrUtil#contains(CharSequence, CharSequence)
     * @since 1.0.0
     */
    public static <T extends CharSequence, X extends Throwable> T notContain( CharSequence textToSearch, T substring, Supplier<X> errorSupplier ) throws X {
        if (StrUtil.contains(textToSearch, substring)) {
            throw errorSupplier.get();
        }
        return substring;
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（即是否为子串）
     *
     * <pre class="code">
     * Assert.notContain(name, "rod", "Name must not contain 'rod'");
     * </pre>
     *
     * @param textToSearch     被搜索的字符串
     * @param substring        被检查的子串
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的子串
     * @throws IllegalArgumentException 非子串抛出异常
     */
    public static String notContain( String textToSearch, String substring, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        return notContain(textToSearch, substring, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（即是否为子串）
     *
     * <pre class="code">
     * Assert.notContain(name, "rod", "Name must not contain 'rod'");
     * </pre>
     *
     * @param textToSearch 被搜索的字符串
     * @param substring    被检查的子串
     * @return 被检查的子串
     * @throws IllegalArgumentException 非子串抛出异常
     */
    public static String notContain( String textToSearch, String substring ) throws IllegalArgumentException {
        return notContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [{}]", substring);
    }

    /**
     * 断言给定数组是否包含元素，数组必须不为 {@code null} 且至少包含一个元素
     * 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.notEmpty(array, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <T>           数组元素类型
     * @param <X>           异常类型
     * @param array         被检查的数组
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查的数组
     * @throws X if the object array is {@code null} or has no elements
     * @see ArrayUtil#isNotEmpty(Object[])
     * @since 1.0.0
     */
    public static <T, X extends Throwable> T[] notEmpty( T[] array, Supplier<X> errorSupplier ) throws X {
        if (ArrayUtil.isEmpty(array)) {
            throw errorSupplier.get();
        }
        return array;
    }

    /**
     * 断言给定数组是否包含元素，数组必须不为 {@code null} 且至少包含一个元素
     *
     * <pre class="code">
     * Assert.notEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param <T>              数组元素类型
     * @param array            被检查的数组
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的数组
     * @throws IllegalArgumentException if the object array is {@code null} or has no elements
     */
    public static <T> T[] notEmpty( T[] array, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        return notEmpty(array, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定数组是否包含元素，数组必须不为 {@code null} 且至少包含一个元素
     *
     * <pre class="code">
     * Assert.notEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 被检查的数组
     * @throws IllegalArgumentException if the object array is {@code null} or has no elements
     */
    public static <T> T[] notEmpty( T[] array ) throws IllegalArgumentException {
        return notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }

    /**
     * 断言给定数组是否不包含{@code null}元素，如果数组为空或 {@code null}将被认为不包含
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.noNullElements(array, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return ");
     *  });
     * </pre>
     *
     * @param <T>           数组元素类型
     * @param <X>           异常类型
     * @param array         被检查的数组
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查的数组
     * @throws X if the object array contains a {@code null} element
     * @see ArrayUtil#hasNull(Object[])
     * @since 1.0.0
     */
    public static <T, X extends Throwable> T[] noNullElements( T[] array, Supplier<X> errorSupplier ) throws X {
        if (ArrayUtil.hasNull(array)) {
            throw errorSupplier.get();
        }
        return array;
    }

    /**
     * 断言给定数组是否不包含{@code null}元素，如果数组为空或 {@code null}将被认为不包含
     *
     * <pre class="code">
     * Assert.noNullElements(array, "The array must have non-null elements");
     * </pre>
     *
     * @param <T>              数组元素类型
     * @param array            被检查的数组
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的数组
     * @throws IllegalArgumentException if the object array contains a {@code null} element
     */
    public static <T> T[] noNullElements( T[] array, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        return noNullElements(array, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定数组是否不包含{@code null}元素，如果数组为空或 {@code null}将被认为不包含
     *
     * <pre class="code">
     * Assert.noNullElements(array);
     * </pre>
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 被检查的数组
     * @throws IllegalArgumentException if the object array contains a {@code null} element
     */
    public static <T> T[] noNullElements( T[] array ) throws IllegalArgumentException {
        return noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
    }

    /**
     * 断言给定集合非空
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.notEmpty(collection, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <E>           集合元素类型
     * @param <T>           集合类型
     * @param <X>           异常类型
     * @param collection    被检查的集合
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 非空集合
     * @throws X if the collection is {@code null} or has no elements
     * @see CollUtil#isNotEmpty(Iterable)
     * @since 1.0.0
     */
    public static <E, T extends Iterable<E>, X extends Throwable> T notEmpty( T collection, Supplier<X> errorSupplier ) throws X {
        if (CollUtil.isEmpty(collection)) {
            throw errorSupplier.get();
        }
        return collection;
    }

    /**
     * 断言给定集合非空
     *
     * <pre class="code">
     * Assert.notEmpty(collection, "Collection must have elements");
     * </pre>
     *
     * @param <E>              集合元素类型
     * @param <T>              集合类型
     * @param collection       被检查的集合
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 非空集合
     * @throws IllegalArgumentException if the collection is {@code null} or has no elements
     */
    public static <E, T extends Iterable<E>> T notEmpty( T collection, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        return notEmpty(collection, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定集合非空
     *
     * <pre class="code">
     * Assert.notEmpty(collection);
     * </pre>
     *
     * @param <E>        集合元素类型
     * @param <T>        集合类型
     * @param collection 被检查的集合
     * @return 被检查集合
     * @throws IllegalArgumentException if the collection is {@code null} or has no elements
     */
    public static <E, T extends Iterable<E>> T notEmpty( T collection ) throws IllegalArgumentException {
        return notEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    /**
     * 断言给定Map非空
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.notEmpty(map, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <K>           Key类型
     * @param <V>           Value类型
     * @param <T>           Map类型
     * @param <X>           异常类型
     * @param map           被检查的Map
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查的Map
     * @throws X if the map is {@code null} or has no entries
     * @see MapUtil#isNotEmpty(Map)
     * @since 1.0.0
     */
    public static <K, V, T extends Map<K, V>, X extends Throwable> T notEmpty( T map, Supplier<X> errorSupplier ) throws X {
        if (MapUtil.isEmpty(map)) {
            throw errorSupplier.get();
        }
        return map;
    }

    /**
     * 断言给定Map非空
     *
     * <pre class="code">
     * Assert.notEmpty(map, "Map must have entries");
     * </pre>
     *
     * @param <K>              Key类型
     * @param <V>              Value类型
     * @param <T>              Map类型
     * @param map              被检查的Map
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的Map
     * @throws IllegalArgumentException if the map is {@code null} or has no entries
     */
    public static <K, V, T extends Map<K, V>> T notEmpty( T map, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        return notEmpty(map, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定Map非空
     *
     * <pre class="code">
     * Assert.notEmpty(map, "Map must have entries");
     * </pre>
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param <T> Map类型
     * @param map 被检查的Map
     * @return 被检查的Map
     * @throws IllegalArgumentException if the map is {@code null} or has no entries
     */
    public static <K, V, T extends Map<K, V>> T notEmpty( T map ) throws IllegalArgumentException {
        return notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }

    /**
     * 断言给定对象是否是给定类的实例
     *
     * <pre class="code">
     * Assert.instanceOf(Foo.class, foo);
     * </pre>
     *
     * @param <T>  被检查对象泛型类型
     * @param type 被检查对象匹配的类型
     * @param obj  被检查对象
     * @return 被检查的对象
     * @throws IllegalArgumentException if the object is not an instance of clazz
     * @see Class#isInstance(Object)
     */
    public static <T> T isInstanceOf( Class<?> type, T obj ) {
        return isInstanceOf(type, obj, "Object [{}] is not instanceof [{}]", obj, type);
    }

    /**
     * 断言给定对象是否是给定类的实例
     *
     * <pre class="code">
     * Assert.instanceOf(Foo.class, foo);
     * </pre>
     *
     * @param <T>              被检查对象泛型类型
     * @param type             被检查对象匹配的类型
     * @param obj              被检查对象
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查对象
     * @throws IllegalArgumentException if the object is not an instance of clazz
     * @see Class#isInstance(Object)
     */
    public static <T> T isInstanceOf( Class<?> type, T obj, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        notNull(type, "Type to check against must not be null");
        if (false == type.isInstance(obj)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        }
        return obj;
    }

    /**
     * 断言 {@code superType.isAssignableFrom(subType)} 是否为 {@code true}.
     *
     * <pre class="code">
     * Assert.isAssignable(Number.class, myClass);
     * </pre>
     *
     * @param superType 需要检查的父类或接口
     * @param subType   需要检查的子类
     * @throws IllegalArgumentException 如果子类非继承父类，抛出此异常
     */
    public static void isAssignable( Class<?> superType, Class<?> subType ) throws IllegalArgumentException {
        isAssignable(superType, subType, "{} is not assignable to {})", subType, superType);
    }

    /**
     * 断言 {@code superType.isAssignableFrom(subType)} 是否为 {@code true}.
     *
     * <pre class="code">
     * Assert.isAssignable(Number.class, myClass);
     * </pre>
     *
     * @param superType        需要检查的父类或接口
     * @param subType          需要检查的子类
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @throws IllegalArgumentException 如果子类非继承父类，抛出此异常
     */
    public static void isAssignable( Class<?> superType, Class<?> subType, String errorMsgTemplate, Object... params ) throws IllegalArgumentException {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        }
    }

    /**
     * 检查boolean表达式，当检查结果为false时抛出 {@code IllegalStateException}。
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.state(id == null, ()-&gt;{
     *      // to query relation message
     *      return "relation message to return ";
     *  });
     * </pre>
     *
     * @param expression       boolean 表达式
     * @param errorMsgSupplier 错误抛出异常附带的消息生产接口
     * @throws IllegalStateException 表达式为 {@code false} 抛出此异常
     */
    public static void state( boolean expression, Supplier<String> errorMsgSupplier ) throws IllegalStateException {
        if (false == expression) {
            throw new IllegalStateException(errorMsgSupplier.get());
        }
    }

    /**
     * 检查boolean表达式，当检查结果为false时抛出 {@code IllegalStateException}。
     *
     * <pre class="code">
     * Assert.state(id == null, "The id property must not already be initialized");
     * </pre>
     *
     * @param expression       boolean 表达式
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @throws IllegalStateException 表达式为 {@code false} 抛出此异常
     */
    public static void state( boolean expression, String errorMsgTemplate, Object... params ) throws IllegalStateException {
        if (false == expression) {
            throw new IllegalStateException(StrUtil.format(errorMsgTemplate, params));
        }
    }

    /**
     * 检查boolean表达式，当检查结果为false时抛出 {@code IllegalStateException}。
     *
     * <pre class="code">
     * Assert.state(id == null);
     * </pre>
     *
     * @param expression boolean 表达式
     * @throws IllegalStateException 表达式为 {@code false} 抛出此异常
     */
    public static void state( boolean expression ) throws IllegalStateException {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }

    /**
     * 检查下标（数组、集合、字符串）是否符合要求，下标必须满足：
     *
     * <pre>
     * 0 &le; index &lt; size
     * </pre>
     *
     * @param index 下标
     * @param size  长度
     * @return 检查后的下标
     * @throws IllegalArgumentException  如果size &lt; 0 抛出此异常
     * @throws IndexOutOfBoundsException 如果index &lt; 0或者 index &ge; size 抛出此异常
     * @since 1.0.0
     */
    public static int checkIndex( int index, int size ) throws IllegalArgumentException, IndexOutOfBoundsException {
        return checkIndex(index, size, "[Assertion failed]");
    }

    /**
     * 检查下标（数组、集合、字符串）是否符合要求，下标必须满足：
     *
     * <pre>
     * 0 &le; index &lt; size
     * </pre>
     *
     * @param index            下标
     * @param size             长度
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 检查后的下标
     * @throws IllegalArgumentException  如果size &lt; 0 抛出此异常
     * @throws IndexOutOfBoundsException 如果index &lt; 0或者 index &ge; size 抛出此异常
     * @since 1.0.0
     */
    public static int checkIndex( int index, int size, String errorMsgTemplate, Object... params ) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(badIndexMsg(index, size, errorMsgTemplate, params));
        }
        return index;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     * @since 1.0.0
     */
    public static int checkBetween( int value, int min, int max ) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(StrUtil.format("Length must be between {} and {}.", min, max));
        }
        return value;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     * @since 1.0.0
     */
    public static long checkBetween( long value, long min, long max ) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(StrUtil.format("Length must be between {} and {}.", min, max));
        }
        return value;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     * @since 1.0.0
     */
    public static double checkBetween( double value, double min, double max ) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(StrUtil.format("Length must be between {} and {}.", min, max));
        }
        return value;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     * @since 1.0.0
     */
    public static Number checkBetween( Number value, Number min, Number max ) {
        notNull(value);
        notNull(min);
        notNull(max);
        double valueDouble = value.doubleValue();
        double minDouble = min.doubleValue();
        double maxDouble = max.doubleValue();
        if (valueDouble < minDouble || valueDouble > maxDouble) {
            throw new IllegalArgumentException(StrUtil.format("Length must be between {} and {}.", min, max));
        }
        return value;
    }


    /**
     * Assert a boolean expression, throwing an {@code IllegalStateException}
     * if the expression evaluates to {@code false}.
     * <p>Call {@link #isTrue} if you wish to throw an {@code IllegalArgumentException}
     * on an assertion failure.
     * <pre class="code">Assert.state(id == null, "The id property must not already be initialized");</pre>
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalStateException if {@code expression} is {@code false}
     */
    public static void state( boolean expression, String message ) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }


    /**
     * Assert that an object is {@code null}.
     * <pre class="code">Assert.isNull(value, "The value must be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull( Object object, String message ) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }


    /**
     * Assert that the given String is not empty; that is,
     * it must not be {@code null} and not the empty String.
     * <pre class="code">Assert.hasLength(name, "Name must not be empty");</pre>
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the text is empty
     * @see StrUtil#hasLength
     */
    public static void hasLength( String text, String message ) {
        if (!StrUtil.hasLength(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given String is not empty; that is,
     * it must not be {@code null} and not the empty String.
     * <pre class="code">
     * Assert.hasLength(account.getName(),
     *     () -&gt; "Name for account '" + account.getId() + "' must not be empty");
     * </pre>
     *
     * @param text            the String to check
     * @param messageSupplier a supplier for the exception message to use if the
     *                        assertion fails
     * @throws IllegalArgumentException if the text is empty
     * @see StrUtil#hasLength
     */
    public static void hasLength( String text, Supplier<String> messageSupplier ) {
        if (!StrUtil.hasLength(text)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }


    /**
     * Assert that the given String contains valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     * <pre class="code">Assert.hasText(name, "'name' must not be empty");</pre>
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the text does not contain valid text content
     * @see StrUtil#hasText
     */
    public static void hasText( String text, String message ) {
        if (!StrUtil.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given String contains valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     * <pre class="code">
     * Assert.hasText(account.getName(),
     *     () -&gt; "Name for account '" + account.getId() + "' must not be empty");
     * </pre>
     *
     * @param text            the String to check
     * @param messageSupplier a supplier for the exception message to use if the
     *                        assertion fails
     * @throws IllegalArgumentException if the text does not contain valid text content
     * @see StrUtil#hasText
     */
    public static void hasText( String text, Supplier<String> messageSupplier ) {
        if (!StrUtil.hasText(text)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }


    /**
     * Assert that the given text does not contain the given substring.
     * <pre class="code">Assert.doesNotContain(name, "rod", "Name must not contain 'rod'");</pre>
     *
     * @param textToSearch the text to search
     * @param substring    the substring to find within the text
     * @param message      the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the text contains the substring
     */
    public static void doesNotContain( String textToSearch, String substring, String message ) {
        if (StrUtil.hasLength(textToSearch) && StrUtil.hasLength(substring) &&
                textToSearch.contains(substring)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given text does not contain the given substring.
     * <pre class="code">
     * Assert.doesNotContain(name, forbidden, () -&gt; "Name must not contain '" + forbidden + "'");
     * </pre>
     *
     * @param textToSearch    the text to search
     * @param substring       the substring to find within the text
     * @param messageSupplier a supplier for the exception message to use if the
     *                        assertion fails
     * @throws IllegalArgumentException if the text contains the substring
     */
    public static void doesNotContain( String textToSearch, String substring, Supplier<String> messageSupplier ) {
        if (StrUtil.hasLength(textToSearch) && StrUtil.hasLength(substring) &&
                textToSearch.contains(substring)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }


    /**
     * Assert that a collection contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">Assert.notEmpty(collection, "Collection must contain elements");</pre>
     *
     * @param collection the collection to check
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the collection is {@code null} or
     *                                  contains no elements
     */
    public static void notEmpty( Collection<?> collection, String message ) {
        if (CollectionUtil.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that a collection contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">
     * Assert.notEmpty(collection, () -&gt; "The " + collectionType + " collection must contain elements");
     * </pre>
     *
     * @param collection      the collection to check
     * @param messageSupplier a supplier for the exception message to use if the
     *                        assertion fails
     * @throws IllegalArgumentException if the collection is {@code null} or
     *                                  contains no elements
     */
    public static void notEmpty( Collection<?> collection, Supplier<String> messageSupplier ) {
        if (CollectionUtil.isEmpty(collection)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }


    /**
     * Assert that a collection contains no {@code null} elements.
     * <p>Note: Does not complain if the collection is empty!
     * <pre class="code">Assert.noNullElements(collection, "Collection must contain non-null elements");</pre>
     *
     * @param collection the collection to check
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the collection contains a {@code null} element
     */
    public static void noNullElements( Collection<?> collection, String message ) {
        if (collection != null) {
            for (Object element : collection) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    /**
     * Assert that a collection contains no {@code null} elements.
     * <p>Note: Does not complain if the collection is empty!
     * <pre class="code">
     * Assert.noNullElements(collection, () -&gt; "Collection " + collectionName + " must contain non-null elements");
     * </pre>
     *
     * @param collection      the collection to check
     * @param messageSupplier a supplier for the exception message to use if the
     *                        assertion fails
     * @throws IllegalArgumentException if the collection contains a {@code null} element
     */
    public static void noNullElements( Collection<?> collection, Supplier<String> messageSupplier ) {
        if (collection != null) {
            for (Object element : collection) {
                if (element == null) {
                    throw new IllegalArgumentException(nullSafeGet(messageSupplier));
                }
            }
        }
    }


    /**
     * Assert that the provided object is an instance of the provided class.
     * <pre class="code">Assert.instanceOf(Foo.class, foo, "Foo expected");</pre>
     *
     * @param type    the type to check against
     * @param obj     the object to check
     * @param message a message which will be prepended to provide further context.
     *                If it is empty or ends in ":" or ";" or "," or ".", a full exception message
     *                will be appended. If it ends in a space, the name of the offending object's
     *                type will be appended. In any other case, a ":" with a space and the name
     *                of the offending object's type will be appended.
     * @throws IllegalArgumentException if the object is not an instance of type
     */
    public static void isInstanceOf( Class<?> type, Object obj, String message ) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            instanceCheckFailed(type, obj, message);
        }
    }

    /**
     * Assert that the provided object is an instance of the provided class.
     * <pre class="code">
     * Assert.instanceOf(Foo.class, foo, () -&gt; "Processing " + Foo.class.getSimpleName() + ":");
     * </pre>
     *
     * @param type            the type to check against
     * @param obj             the object to check
     * @param messageSupplier a supplier for the exception message to use if the
     *                        assertion fails. See {@link #isInstanceOf(Class, Object, String)} for details.
     * @throws IllegalArgumentException if the object is not an instance of type
     */
    public static void isInstanceOf( Class<?> type, Object obj, Supplier<String> messageSupplier ) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            instanceCheckFailed(type, obj, nullSafeGet(messageSupplier));
        }
    }


    /**
     * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
     * <pre class="code">Assert.isAssignable(Number.class, myClass, "Number expected");</pre>
     *
     * @param superType the super type to check against
     * @param subType   the sub type to check
     * @param message   a message which will be prepended to provide further context.
     *                  If it is empty or ends in ":" or ";" or "," or ".", a full exception message
     *                  will be appended. If it ends in a space, the name of the offending sub type
     *                  will be appended. In any other case, a ":" with a space and the name of the
     *                  offending sub type will be appended.
     * @throws IllegalArgumentException if the classes are not assignable
     */
    public static void isAssignable( Class<?> superType, Class<?> subType, String message ) {
        notNull(superType, "Super type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            assignableCheckFailed(superType, subType, message);
        }
    }

    /**
     * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
     * <pre class="code">
     * Assert.isAssignable(Number.class, myClass, () -&gt; "Processing " + myAttributeName + ":");
     * </pre>
     *
     * @param superType       the super type to check against
     * @param subType         the sub type to check
     * @param messageSupplier a supplier for the exception message to use if the
     *                        assertion fails. See {@link #isAssignable(Class, Class, String)} for details.
     * @throws IllegalArgumentException if the classes are not assignable
     */
    public static void isAssignable( Class<?> superType, Class<?> subType, Supplier<String> messageSupplier ) {
        notNull(superType, "Super type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            assignableCheckFailed(superType, subType, nullSafeGet(messageSupplier));
        }
    }


    private static void instanceCheckFailed( Class<?> type, Object obj, String msg ) {
        String className = (obj != null ? obj.getClass().getName() : "null");
        String result = "";
        boolean defaultMessage = true;
        if (StrUtil.hasLength(msg)) {
            if (endsWithSeparator(msg)) {
                result = msg + " ";
            } else {
                result = messageWithTypeName(msg, className);
                defaultMessage = false;
            }
        }
        if (defaultMessage) {
            result = result + ("Object of class [" + className + "] must be an instance of " + type);
        }
        throw new IllegalArgumentException(result);
    }

    private static void assignableCheckFailed( Class<?> superType, Class<?> subType, String msg ) {
        String result = "";
        boolean defaultMessage = true;
        if (StrUtil.hasLength(msg)) {
            if (endsWithSeparator(msg)) {
                result = msg + " ";
            } else {
                result = messageWithTypeName(msg, subType);
                defaultMessage = false;
            }
        }
        if (defaultMessage) {
            result = result + (subType + " is not assignable to " + superType);
        }
        throw new IllegalArgumentException(result);
    }

    private static boolean endsWithSeparator( String msg ) {
        return (msg.endsWith(":") || msg.endsWith(";") || msg.endsWith(",") || msg.endsWith("."));
    }

    private static String messageWithTypeName( String msg, Object typeName ) {
        return msg + (msg.endsWith(" ") ? "" : ": ") + typeName;
    }


    private static String nullSafeGet( Supplier<String> messageSupplier ) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }


    // TODO(cpovirk): Standardize parameter names (expression vs. b, reference vs. obj).

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument( boolean expression ) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression   a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument( boolean expression, Object errorMessage ) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression           a boolean expression
     * @param errorMessageTemplate a template for the exception message should the check fail. The
     *                             message is formed by replacing each {@code %s} placeholder in the template with an
     *                             argument. These are matched by position - the first {@code %s} gets {@code
     *                             errorMessageArgs[0]}, etc. Unmatched arguments will be appended to the formatted message in
     *                             square braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments
     *                             are converted to StrUtil using {@link String#valueOf(Object)}.
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument(
            boolean expression,
            String errorMessageTemplate,
            Object... errorMessageArgs ) {
        if (!expression) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, errorMessageArgs));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param errorMessageTemplate errorMessageTemplate
     * @param p1                   p1
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, char p1 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, int p1 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param p1                   p1
     * @param b                    b
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, long p1 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param errorMessageTemplate errorMessageTemplate
     * @param b                    b
     * @param p1                   p1
     */
    public static void checkArgument(
            boolean b, String errorMessageTemplate, Object p1 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, char p1, char p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, char p1, int p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, char p1, long p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument(
            boolean b, String errorMessageTemplate, char p1, Object p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, int p1, char p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, int p1, int p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, int p1, long p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument(
            boolean b, String errorMessageTemplate, int p1, Object p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, long p1, char p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, long p1, int p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument( boolean b, String errorMessageTemplate, long p1, long p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument(
            boolean b, String errorMessageTemplate, long p1, Object p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument(
            boolean b, String errorMessageTemplate, Object p1, char p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument(
            boolean b, String errorMessageTemplate, Object p1, int p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument(
            boolean b, String errorMessageTemplate, Object p1, long p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument(
            boolean b, String errorMessageTemplate, Object p1, Object p2 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param p3                   p3
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument(
            boolean b,
            String errorMessageTemplate,
            Object p1,
            Object p2,
            Object p3 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2, p3));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * <p>See {@link #checkArgument(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param p3                   p3
     * @param p4                   p4
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkArgument(
            boolean b,
            String errorMessageTemplate,
            Object p1,
            Object p2,
            Object p3,
            Object p4 ) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2, p3, p4));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void checkState( boolean expression ) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression   a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void checkState( boolean expression, Object errorMessage ) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression           a boolean expression
     * @param errorMessageTemplate a template for the exception message should the check fail. The
     *                             message is formed by replacing each {@code %s} placeholder in the template with an
     *                             argument. These are matched by position - the first {@code %s} gets {@code
     *                             errorMessageArgs[0]}, etc. Unmatched arguments will be appended to the formatted message in
     *                             square braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments
     *                             are converted to StrUtil using {@link String#valueOf(Object)}.
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void checkState(
            boolean expression,
            /*
             * TODO(cpovirk): Consider removing  here, as we've done with the other methods'
             * errorMessageTemplate parameters: It it unlikely that callers intend for their string
             * template to be null (though we do handle that case gracefully at runtime). I've left this
             * one as it is because one of our users has defined a wrapper API around Precondition,
             * declaring a checkState method that accepts a possibly null template. So we'd need to update
             * that user first.
             */
            String errorMessageTemplate,
            Object... errorMessageArgs ) {
        if (!expression) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, errorMessageArgs));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, char p1 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, int p1 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, long p1 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, Object p1 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, char p1, char p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, char p1, int p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, char p1, long p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState(
            boolean b, String errorMessageTemplate, char p1, Object p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, int p1, char p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, int p1, int p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, int p1, long p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState(
            boolean b, String errorMessageTemplate, int p1, Object p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, long p1, char p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, long p1, int p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState( boolean b, String errorMessageTemplate, long p1, long p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState(
            boolean b, String errorMessageTemplate, long p1, Object p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState(
            boolean b, String errorMessageTemplate, Object p1, char p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState(
            boolean b, String errorMessageTemplate, Object p1, int p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState(
            boolean b, String errorMessageTemplate, Object p1, long p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState(
            boolean b, String errorMessageTemplate, Object p1, Object p2 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param p3                   p3
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState(
            boolean b,
            String errorMessageTemplate,
            Object p1,
            Object p2,
            Object p3 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2, p3));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * <p>See {@link #checkState(boolean, String, Object...)} for details.
     *
     * @param b                    b
     * @param p1                   p1
     * @param p2                   p2
     * @param p3                   p3
     * @param p4                   P4
     * @param errorMessageTemplate errorMessageTemplate
     */
    public static void checkState(
            boolean b,
            String errorMessageTemplate,
            Object p1,
            Object p2,
            Object p3,
            Object p4 ) {
        if (!b) {
            throw new IllegalStateException(lenientFormat(errorMessageTemplate, p1, p2, p3, p4));
        }
    }

    /*
     * Precondition.checkNotNull is *intended* for performing eager null checks on parameters that a
     * nullness checker can already "prove" are non-null. That means that the first parameter to
     * checkNotNull *should* be annotated to require it to be non-null.
     *
     * However, for a variety of reasons, Google developers have written a ton of code over the past
     * decade that assumes that they can use checkNotNull for non-precondition checks. I had hoped to
     * take a principled stand on this, but the amount of such code is simply overwhelming. To avoid
     * creating a lot of compile errors that users would not find to be informative, we're giving in
     * and allowing callers to pass arguments that a nullness checker believes could be null.
     *
     * We still encourage people to use requireNonNull over checkNotNull for non-precondition checks.
     */

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @param <T>       T
     * @return T 返回泛型的结果
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull( T reference ) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference    an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @param <T>          T
     * @return T 返回泛型的结果
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull( T reference, Object errorMessage ) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference            an object reference
     * @param errorMessageTemplate a template for the exception message should the check fail. The
     *                             message is formed by replacing each {@code %s} placeholder in the template with an
     *                             argument. These are matched by position - the first {@code %s} gets {@code
     *                             errorMessageArgs[0]}, etc. Unmatched arguments will be appended to the formatted message in
     *                             square braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments
     *                             are converted to StrUtil using {@link String#valueOf(Object)}.
     * @param <T>                  T
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */

    public static <T> T checkNotNull(
            T reference,
            String errorMessageTemplate,
            Object... errorMessageArgs ) {
        if (reference == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return T 返回泛型的结果
     */

    public static <T> T checkNotNull( T obj, String errorMessageTemplate, char p1 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return T 返回泛型的结果
     */

    public static <T> T checkNotNull( T obj, String errorMessageTemplate, int p1 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return T 返回泛型的结果
     */

    public static <T> T checkNotNull( T obj, String errorMessageTemplate, long p1 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return T 返回泛型的结果
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, Object p1 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return T 返回泛型的结果
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, char p1, char p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return T 返回泛型的结果
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, char p1, int p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return T 返回泛型的结果
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, char p1, long p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return T 返回泛型的结果
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, char p1, Object p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, int p1, char p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, int p1, int p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, int p1, long p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, int p1, Object p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, long p1, char p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, long p1, int p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, long p1, long p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, long p1, Object p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, Object p1, char p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, Object p1, int p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */

    public static <T> T checkNotNull(
            T obj, String errorMessageTemplate, Object p1, long p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */
    public static <T> T checkNotNull(
            T obj,
            String errorMessageTemplate,
            Object p1,
            Object p2 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param p3                   p3
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */
    public static <T> T checkNotNull(
            T obj,
            String errorMessageTemplate,
            Object p1,
            Object p2,
            Object p3 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2, p3));
        }
        return obj;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * <p>See {@link #checkNotNull(Object, String, Object...)} for details.
     *
     * @param obj                  obj
     * @param p1                   p1
     * @param p2                   p2
     * @param p3                   p3
     * @param p4                   p4
     * @param <T>                  T
     * @param errorMessageTemplate errorMessageTemplate
     * @return 返回T
     */
    public static <T> T checkNotNull(
            T obj,
            String errorMessageTemplate,
            Object p1,
            Object p2,
            Object p3,
            Object p4 ) {
        if (obj == null) {
            throw new NullPointerException(lenientFormat(errorMessageTemplate, p1, p2, p3, p4));
        }
        return obj;
    }

    /*
     * All recent hotspots (as of 2009) *really* like to have the natural code
     *
     * if (guardExpression) {
     *    throw new BadException(messageExpression);
     * }
     *
     * refactored so that messageExpression is moved to a separate String-returning method.
     *
     * if (guardExpression) {
     *    throw new BadException(badMsg(...));
     * }
     *
     * The alternative natural refactorings into void or Exception-returning methods are much slower.
     * This is a big deal - we're talking factors of 2-8 in microbenchmarks, not just 10-20%. (This is
     * a hotspot optimizer bug, which should be fixed, but that's a separate, big project).
     *
     * The coding pattern above is heavily used in java.util, e.g. in ArrayList. There is a
     * RangeCheckMicroBenchmark in the JDK that was used to test this.
     *
     * But the methods in this class want to throw different exceptions, depending on the args, so it
     * appears that this pattern is not directly applicable. But we can use the ridiculous, devious
     * trick of throwing an exception in the middle of the construction of another exception. Hotspot
     * is fine with that.
     */

    /**
     * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size
     * {@code size}. An element index may range from zero, inclusive, to {@code size}, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size  the size of that array, list or string
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than {@code size}
     * @throws IllegalArgumentException  if {@code size} is negative
     */

    public static int checkElementIndex( int index, int size ) {
        return checkElementIndex(index, size, "index");
    }

    /**
     * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size
     * {@code size}. An element index may range from zero, inclusive, to {@code size}, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size  the size of that array, list or string
     * @param desc  the text to use to describe this index in an error message
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than {@code size}
     * @throws IllegalArgumentException  if {@code size} is negative
     */

    public static int checkElementIndex( int index, int size, String desc ) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(badElementIndex(index, size, desc));
        }
        return index;
    }

    private static String badElementIndex( int index, int size, String desc ) {
        if (index < 0) {
            return lenientFormat("%s (%s) must not be negative", desc, index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else { // index >= size
            return lenientFormat("%s (%s) must be less than size (%s)", desc, index, size);
        }
    }

    /**
     * Ensures that {@code index} specifies a valid <i>position</i> in an array, list or string of
     * size {@code size}. A position index may range from zero to {@code size}, inclusive.
     *
     * @param index a user-supplied index identifying a position in an array, list or string
     * @param size  the size of that array, list or string
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is greater than {@code size}
     * @throws IllegalArgumentException  if {@code size} is negative
     */

    public static int checkPositionIndex( int index, int size ) {
        return checkPositionIndex(index, size, "index");
    }

    /**
     * Ensures that {@code index} specifies a valid <i>position</i> in an array, list or string of
     * size {@code size}. A position index may range from zero to {@code size}, inclusive.
     *
     * @param index a user-supplied index identifying a position in an array, list or string
     * @param size  the size of that array, list or string
     * @param desc  the text to use to describe this index in an error message
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is greater than {@code size}
     * @throws IllegalArgumentException  if {@code size} is negative
     */

    public static int checkPositionIndex( int index, int size, String desc ) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(badPositionIndex(index, size, desc));
        }
        return index;
    }

    /**
     * @param key   key
     * @param value value
     */
    public static void checkEntryNotNull( Object key, Object value ) {
        if (key == null) {
            throw new NullPointerException("null key in entry: null=" + value);
        } else if (value == null) {
            throw new NullPointerException("null value in entry: " + key + "=null");
        }
    }

    /**
     * @param value value
     * @param name  name
     * @return return
     */
    public static int checkNonnegative( int value, String name ) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    /**
     * @param value value
     * @param name  name
     * @return return
     */
    public static long checkNonnegative( long value, String name ) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    public static int checkPositive( String role, int x ) {
        if (x <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }

    public static long checkPositive( String role, long x ) {
        if (x <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }

    public static BigInteger checkPositive( String role, BigInteger x ) {
        if (x.signum() <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }

    /**
     * @param value value
     * @param name  name
     */
    public static void checkPositive( int value, String name ) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive but was: " + value);
        }
    }

    /**
     * Precondition tester for {@code Iterator.remove()} that throws an exception with a consistent
     * error message.
     *
     * @param canRemove canRemove
     */


    public static void checkRemove( boolean canRemove ) {
        checkState(canRemove, "no calls to next() since the last call to remove()");
    }

    // -------------------------------------------------------------------------------------------------------------------------------------------- Private method start


    /**
     * Ensures that {@code index} specifies a valid <i>position</i> in an array, list or string of
     * size {@code size}. A position index may range from zero to {@code size}, inclusive.
     *
     * @param index a user-supplied index identifying a position in an array, list or string
     * @param size  the size of that array, list or string
     * @param desc  the text to use to describe this index in an error message
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is greater than {@code size}
     * @throws IllegalArgumentException  if {@code size} is negative
     */
    private static String badPositionIndex( int index, int size, String desc ) {
        if (index < 0) {
            return lenientFormat("%s (%s) must not be negative", desc, index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else { // index > size
            return lenientFormat("%s (%s) must not be greater than size (%s)", desc, index, size);
        }
    }

    /**
     * Ensures that {@code start} and {@code end} specify valid <i>positions</i> in an array, list or
     * string of size {@code size}, and are in order. A position index may range from zero to {@code
     * size}, inclusive.
     *
     * @param start a user-supplied index identifying a starting position in an array, list or string
     * @param end   a user-supplied index identifying an ending position in an array, list or string
     * @param size  the size of that array, list or string
     * @throws IndexOutOfBoundsException if either index is negative or is greater than {@code size},
     *                                   or if {@code end} is less than {@code start}
     * @throws IllegalArgumentException  if {@code size} is negative
     */
    public static void checkPositionIndexes( int start, int end, int size ) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (start < 0 || end < start || end > size) {
            throw new IndexOutOfBoundsException(badPositionIndexes(start, end, size));
        }
    }

    private static String badPositionIndexes( int start, int end, int size ) {
        if (start < 0 || start > size) {
            return badPositionIndex(start, size, "start index");
        }
        if (end < 0 || end > size) {
            return badPositionIndex(end, size, "end index");
        }
        // end < start
        return lenientFormat("end index (%s) must not be less than start index (%s)", end, start);
    }

    public static int checkNonNegative( String role, int x ) {
        if (x < 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }

    public static long checkNonNegative( String role, long x ) {
        if (x < 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }

    public static BigInteger checkNonNegative( String role, BigInteger x ) {
        if (x.signum() < 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }

    public static double checkNonNegative( String role, double x ) {
        if (!(x >= 0)) { // not x < 0, to work with NaN.
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }


    /**
     * 错误的下标时显示的消息
     *
     * @param index  下标
     * @param size   长度
     * @param desc   异常时的消息模板
     * @param params 参数列表
     * @return 消息
     */
    private static String badIndexMsg( int index, int size, String desc, Object... params ) {
        if (index < 0) {
            return StrUtil.format("{} ({}) must not be negative", StrUtil.format(desc, params), index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else { // index >= size
            return StrUtil.format("{} ({}) must be less than size ({})", StrUtil.format(desc, params), index, size);
        }
    }
    // -------------------------------------------------------------------------------------------------------------------------------------------- Private method end
}
