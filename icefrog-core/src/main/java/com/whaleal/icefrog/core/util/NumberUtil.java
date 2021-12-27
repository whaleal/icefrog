package com.whaleal.icefrog.core.util;


import com.whaleal.icefrog.core.exceptions.UtilException;
import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.math.Calculator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;
import static com.whaleal.icefrog.core.lang.Precondition.checkNonNegative;
import static com.whaleal.icefrog.core.math.MathUtil.*;

/**
 * Miscellaneous utility methods for number conversion and parsing.
 * <p>Mainly for internal use within the framework; consider Apache's
 * Commons Lang for a more comprehensive suite of number utilities.
 * <p>
 * <p>
 * 数字工具类<br>
 * 对于精确值计算应该使用 {@link BigDecimal}<br>
 * JDK7中<strong>BigDecimal(double val)</strong>构造方法的结果有一定的不可预知性，例如：
 *
 * <pre>
 * new BigDecimal(0.1)
 * </pre>
 * <p>
 * 表示的不是<strong>0.1</strong>而是<strong>0.1000000000000000055511151231257827021181583404541015625</strong>
 *
 * <p>
 * 这是因为0.1无法准确的表示为double。因此应该使用<strong>new BigDecimal(String)</strong>。
 * </p>
 * 相关介绍：
 * <ul>
 * <li>http://www.oschina.net/code/snippet_563112_25237</li>
 * <li>https://github.com/venusdrogon/feilong-core/wiki/one-jdk7-bug-thinking</li>
 * </ul>
 *
 * @author Looly
 * @author wh
 */
public class NumberUtil {

    public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);
    /**
     * Standard number types (all immutable):
     * Byte, Short, Integer, Long, BigInteger, Float, Double, BigDecimal.
     */
    public static final Set<Class<?>> STANDARD_NUMBER_TYPES;
    /**
     * 默认除法运算精度
     */
    private static final int DEFAULT_DIV_SCALE = 10;
    /**
     * 0-20对应的阶乘，超过20的阶乘会超过Long.MAX_VALUE
     */
    private static final long[] FACTORIALS = new long[]{
            1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L,
            87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L,
            2432902008176640000L};
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    // binomial(biggestBinomials[k], k) fits in an int, but not binomial(biggestBinomials[k]+1,k).
    static int[] biggestBinomials = {
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            65536,
            2345,
            477,
            193,
            110,
            75,
            58,
            49,
            43,
            39,
            37,
            35,
            34,
            34,
            33
    };

    static {
        Set<Class<?>> numberTypes = new HashSet<>(8);
        numberTypes.add(Byte.class);
        numberTypes.add(Short.class);
        numberTypes.add(Integer.class);
        numberTypes.add(Long.class);
        numberTypes.add(BigInteger.class);
        numberTypes.add(Float.class);
        numberTypes.add(Double.class);
        numberTypes.add(BigDecimal.class);
        STANDARD_NUMBER_TYPES = Collections.unmodifiableSet(numberTypes);
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static double add(float v1, float v2) {
        return add(Float.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static double add(float v1, double v2) {
        return add(Float.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static double add(double v1, float v2) {
        return add(Double.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static double add(double v1, double v2) {
        return add(Double.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     * @since 1.0.0
     */
    public static double add(Double v1, Double v2) {
        //noinspection RedundantCast
        return add((Number) v1, (Number) v2).doubleValue();
    }

    /**
     * 提供精确的加法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static BigDecimal add(Number v1, Number v2) {
        return add(new Number[]{v1, v2});
    }

    /**
     * 提供精确的加法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被加值
     * @return 和
     * @since 1.0.0
     */
    public static BigDecimal add(Number... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        Number value = values[0];
        BigDecimal result = toBigDecimal(value);
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.add(toBigDecimal(value));
            }
        }
        return result;
    }

    /**
     * 提供精确的加法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被加值
     * @return 和
     * @since 1.0.0
     */
    public static BigDecimal add(String... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        String value = values[0];
        BigDecimal result = toBigDecimal(value);
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (StrUtil.isNotBlank(value)) {
                result = result.add(toBigDecimal(value));
            }
        }
        return result;
    }

    /**
     * 提供精确的加法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被加值
     * @return 和
     * @since 1.0.0
     */
    public static BigDecimal add(BigDecimal... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        BigDecimal value = values[0];
        BigDecimal result = toBigDecimal(value);
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.add(value);
            }
        }
        return result;
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(float v1, float v2) {
        return sub(Float.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(float v1, double v2) {
        return sub(Float.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(double v1, float v2) {
        return sub(Double.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(double v1, double v2) {
        return sub(Double.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(Double v1, Double v2) {
        //noinspection RedundantCast
        return sub((Number) v1, (Number) v2).doubleValue();
    }

    /**
     * 提供精确的减法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static BigDecimal sub(Number v1, Number v2) {
        return sub(new Number[]{v1, v2});
    }

    /**
     * 提供精确的减法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被减值
     * @return 差
     * @since 1.0.0
     */
    public static BigDecimal sub(Number... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        Number value = values[0];
        BigDecimal result = toBigDecimal(value);
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.subtract(toBigDecimal(value));
            }
        }
        return result;
    }

    /**
     * 提供精确的减法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被减值
     * @return 差
     * @since 1.0.0
     */
    public static BigDecimal sub(String... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        String value = values[0];
        BigDecimal result = toBigDecimal(value);
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (StrUtil.isNotBlank(value)) {
                result = result.subtract(toBigDecimal(value));
            }
        }
        return result;
    }

    /**
     * 提供精确的减法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被减值
     * @return 差
     * @since 1.0.0
     */
    public static BigDecimal sub(BigDecimal... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        BigDecimal value = values[0];
        BigDecimal result = toBigDecimal(value);
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.subtract(value);
            }
        }
        return result;
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(float v1, float v2) {
        return mul(Float.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(float v1, double v2) {
        return mul(Float.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(double v1, float v2) {
        return mul(Double.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(double v1, double v2) {
        return mul(Double.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(Double v1, Double v2) {
        //noinspection RedundantCast
        return mul((Number) v1, (Number) v2).doubleValue();
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static BigDecimal mul(Number v1, Number v2) {
        return mul(new Number[]{v1, v2});
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被乘值
     * @return 积
     * @since 1.0.0
     */
    public static BigDecimal mul(Number... values) {
        if (ArrayUtil.isEmpty(values) || ArrayUtil.hasNull(values)) {
            return BigDecimal.ZERO;
        }

        Number value = values[0];
        BigDecimal result = new BigDecimal(value.toString());
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            result = result.multiply(new BigDecimal(value.toString()));
        }
        return result;
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     * @since 1.0.0
     */
    public static BigDecimal mul(String v1, String v2) {
        return mul(new BigDecimal(v1), new BigDecimal(v2));
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被乘值
     * @return 积
     * @since 1.0.0
     */
    public static BigDecimal mul(String... values) {
        if (ArrayUtil.isEmpty(values) || ArrayUtil.hasNull(values)) {
            return BigDecimal.ZERO;
        }

        BigDecimal result = new BigDecimal(values[0]);
        for (int i = 1; i < values.length; i++) {
            result = result.multiply(new BigDecimal(values[i]));
        }

        return result;
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被乘值
     * @return 积
     * @since 1.0.0
     */
    public static BigDecimal mul(BigDecimal... values) {
        if (ArrayUtil.isEmpty(values) || ArrayUtil.hasNull(values)) {
            return BigDecimal.ZERO;
        }

        BigDecimal result = values[0];
        for (int i = 1; i < values.length; i++) {
            result = result.multiply(values[i]);
        }
        return result;
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(float v1, float v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(float v1, double v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1, float v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(Double v1, Double v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     * @since 1.0.0
     */
    public static BigDecimal div(Number v1, Number v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal div(String v1, String v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(float v1, float v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(float v1, double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(double v1, float v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(Double v1, Double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     * @since 1.0.0
     */
    public static BigDecimal div(Number v1, Number v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static BigDecimal div(String v1, String v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(float v1, float v2, int scale, RoundingMode roundingMode) {
        return div(Float.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(float v1, double v2, int scale, RoundingMode roundingMode) {
        return div(Float.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(double v1, float v2, int scale, RoundingMode roundingMode) {
        return div(Double.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
    }

    // ------------------------------------------------------------------------------------------- round

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale, RoundingMode roundingMode) {
        return div(Double.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(Double v1, Double v2, int scale, RoundingMode roundingMode) {
        //noinspection RedundantCast
        return div((Number) v1, (Number) v2, scale, roundingMode).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     * @since 1.0.0
     */
    public static BigDecimal div(Number v1, Number v2, int scale, RoundingMode roundingMode) {
        return div(v1.toString(), v2.toString(), scale, roundingMode);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static BigDecimal div(String v1, String v2, int scale, RoundingMode roundingMode) {
        return div(toBigDecimal(v1), toBigDecimal(v2), scale, roundingMode);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     * @since 1.0.0
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale, RoundingMode roundingMode) {
        Precondition.notNull(v2, "Divisor must be not null !");
        if (null == v1) {
            return BigDecimal.ZERO;
        }
        if (scale < 0) {
            scale = -scale;
        }
        return v1.divide(v2, scale, roundingMode);
    }

    /**
     * 补充Math.ceilDiv() JDK8中添加了和Math.floorDiv()但却没有ceilDiv()
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     * @since 1.0.0
     */
    public static int ceilDiv(int v1, int v2) {
        return (int) Math.ceil((double) v1 / v2);
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param v     值
     * @param scale 保留小数位数
     * @return 新值
     */
    public static BigDecimal round(double v, int scale) {
        return round(v, scale, RoundingMode.HALF_UP);
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param v     值
     * @param scale 保留小数位数
     * @return 新值
     */
    public static String roundStr(double v, int scale) {
        return round(v, scale).toString();
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param numberStr 数字值的字符串表现形式
     * @param scale     保留小数位数
     * @return 新值
     */
    public static BigDecimal round(String numberStr, int scale) {
        return round(numberStr, scale, RoundingMode.HALF_UP);
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param number 数字值
     * @param scale  保留小数位数
     * @return 新值
     * @since 1.0.0
     */
    public static BigDecimal round(BigDecimal number, int scale) {
        return round(number, scale, RoundingMode.HALF_UP);
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param numberStr 数字值的字符串表现形式
     * @param scale     保留小数位数
     * @return 新值
     * @since 1.0.0
     */
    public static String roundStr(String numberStr, int scale) {
        return round(numberStr, scale).toString();
    }

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param v            值
     * @param scale        保留小数位数
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 新值
     */
    public static BigDecimal round(double v, int scale, RoundingMode roundingMode) {
        return round(Double.toString(v), scale, roundingMode);
    }

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param v            值
     * @param scale        保留小数位数
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 新值
     * @since 1.0.0
     */
    public static String roundStr(double v, int scale, RoundingMode roundingMode) {
        return round(v, scale, roundingMode).toString();
    }

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param numberStr    数字值的字符串表现形式
     * @param scale        保留小数位数，如果传入小于0，则默认0
     * @param roundingMode 保留小数的模式 {@link RoundingMode}，如果传入null则默认四舍五入
     * @return 新值
     */
    public static BigDecimal round(String numberStr, int scale, RoundingMode roundingMode) {
        Precondition.notBlank(numberStr);
        if (scale < 0) {
            scale = 0;
        }
        return round(toBigDecimal(numberStr), scale, roundingMode);
    }

    // ------------------------------------------------------------------------------------------- decimalFormat

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param number       数字值
     * @param scale        保留小数位数，如果传入小于0，则默认0
     * @param roundingMode 保留小数的模式 {@link RoundingMode}，如果传入null则默认四舍五入
     * @return 新值
     */
    public static BigDecimal round(BigDecimal number, int scale, RoundingMode roundingMode) {
        if (null == number) {
            number = BigDecimal.ZERO;
        }
        if (scale < 0) {
            scale = 0;
        }
        if (null == roundingMode) {
            roundingMode = RoundingMode.HALF_UP;
        }

        return number.setScale(scale, roundingMode);
    }

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param numberStr    数字值的字符串表现形式
     * @param scale        保留小数位数
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 新值
     * @since 1.0.0
     */
    public static String roundStr(String numberStr, int scale, RoundingMode roundingMode) {
        return round(numberStr, scale, roundingMode).toString();
    }

    /**
     * 四舍六入五成双计算法
     * <p>
     * 四舍六入五成双是一种比较精确比较科学的计数保留法，是一种数字修约规则。
     * </p>
     *
     * <pre>
     * 算法规则:
     * 四舍六入五考虑，
     * 五后非零就进一，
     * 五后皆零看奇偶，
     * 五前为偶应舍去，
     * 五前为奇要进一。
     * </pre>
     *
     * @param number 需要科学计算的数据
     * @param scale  保留的小数位
     * @return 结果
     * @since 1.0.0
     */
    public static BigDecimal roundHalfEven(Number number, int scale) {
        return roundHalfEven(toBigDecimal(number), scale);
    }

    /**
     * 四舍六入五成双计算法
     * <p>
     * 四舍六入五成双是一种比较精确比较科学的计数保留法，是一种数字修约规则。
     * </p>
     *
     * <pre>
     * 算法规则:
     * 四舍六入五考虑，
     * 五后非零就进一，
     * 五后皆零看奇偶，
     * 五前为偶应舍去，
     * 五前为奇要进一。
     * </pre>
     *
     * @param value 需要科学计算的数据
     * @param scale 保留的小数位
     * @return 结果
     * @since 1.0.0
     */
    public static BigDecimal roundHalfEven(BigDecimal value, int scale) {
        return round(value, scale, RoundingMode.HALF_EVEN);
    }

    /**
     * 保留固定小数位数，舍去多余位数
     *
     * @param number 需要科学计算的数据
     * @param scale  保留的小数位
     * @return 结果
     * @since 1.0.0
     */
    public static BigDecimal roundDown(Number number, int scale) {
        return roundDown(toBigDecimal(number), scale);
    }

    /**
     * 保留固定小数位数，舍去多余位数
     *
     * @param value 需要科学计算的数据
     * @param scale 保留的小数位
     * @return 结果
     * @since 1.0.0
     */
    public static BigDecimal roundDown(BigDecimal value, int scale) {
        return round(value, scale, RoundingMode.DOWN);
    }

    // ------------------------------------------------------------------------------------------- isXXX

    /**
     * 格式化double<br>
     * 对 {@link DecimalFormat} 做封装<br>
     *
     * @param pattern 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
     *                <ul>
     *                <li>0 =》 取一位整数</li>
     *                <li>0.00 =》 取一位整数和两位小数</li>
     *                <li>00.000 =》 取两位整数和三位小数</li>
     *                <li># =》 取所有整数部分</li>
     *                <li>#.##% =》 以百分比方式计数，并取两位小数</li>
     *                <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
     *                <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
     *                <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
     *                </ul>
     * @param value   值
     * @return 格式化后的值
     */
    public static String decimalFormat(String pattern, double value) {
        Precondition.isTrue(isValid(value), "value is NaN or Infinite!");
        return new DecimalFormat(pattern).format(value);
    }

    /**
     * 格式化double<br>
     * 对 {@link DecimalFormat} 做封装<br>
     *
     * @param pattern 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
     *                <ul>
     *                <li>0 =》 取一位整数</li>
     *                <li>0.00 =》 取一位整数和两位小数</li>
     *                <li>00.000 =》 取两位整数和三位小数</li>
     *                <li># =》 取所有整数部分</li>
     *                <li>#.##% =》 以百分比方式计数，并取两位小数</li>
     *                <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
     *                <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
     *                <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
     *                </ul>
     * @param value   值
     * @return 格式化后的值
     * @since 1.0.0
     */
    public static String decimalFormat(String pattern, long value) {
        return new DecimalFormat(pattern).format(value);
    }

    /**
     * 格式化double<br>
     * 对 {@link DecimalFormat} 做封装<br>
     *
     * @param pattern 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
     *                <ul>
     *                <li>0 =》 取一位整数</li>
     *                <li>0.00 =》 取一位整数和两位小数</li>
     *                <li>00.000 =》 取两位整数和三位小数</li>
     *                <li># =》 取所有整数部分</li>
     *                <li>#.##% =》 以百分比方式计数，并取两位小数</li>
     *                <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
     *                <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
     *                <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
     *                </ul>
     * @param value   值，支持BigDecimal、BigInteger、Number等类型
     * @return 格式化后的值
     * @since 1.0.0
     */
    public static String decimalFormat(String pattern, Object value) {
        return decimalFormat(pattern, value, null);
    }

    /**
     * 格式化double<br>
     * 对 {@link DecimalFormat} 做封装<br>
     *
     * @param pattern      格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
     *                     <ul>
     *                     <li>0 =》 取一位整数</li>
     *                     <li>0.00 =》 取一位整数和两位小数</li>
     *                     <li>00.000 =》 取两位整数和三位小数</li>
     *                     <li># =》 取所有整数部分</li>
     *                     <li>#.##% =》 以百分比方式计数，并取两位小数</li>
     *                     <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
     *                     <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
     *                     <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
     *                     </ul>
     * @param value        值，支持BigDecimal、BigInteger、Number等类型
     * @param roundingMode 保留小数的方式枚举
     * @return 格式化后的值
     * @since 1.0.0
     */
    public static String decimalFormat(String pattern, Object value, RoundingMode roundingMode) {
        if (value instanceof Number) {
            Precondition.isTrue(isValidNumber((Number) value), "value is NaN or Infinite!");
        }
        final DecimalFormat decimalFormat = new DecimalFormat(pattern);
        if (null != roundingMode) {
            decimalFormat.setRoundingMode(roundingMode);
        }
        return decimalFormat.format(value);
    }

    /**
     * 格式化金额输出，每三位用逗号分隔
     *
     * @param value 金额
     * @return 格式化后的值
     * @since 1.0.0
     */
    public static String decimalFormatMoney(double value) {
        return decimalFormat(",##0.00", value);
    }

    // ------------------------------------------------------------------------------------------- generateXXX

    /**
     * 格式化百分比，小数采用四舍五入方式
     *
     * @param number 值
     * @param scale  保留小数位数
     * @return 百分比
     * @since 1.0.0
     */
    public static String formatPercent(double number, int scale) {
        final NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(scale);
        return format.format(number);
    }

    /**
     * 是否为数字，支持包括：
     *
     * <pre>
     * 1、10进制
     * 2、16进制数字（0x开头）
     * 3、科学计数法形式（1234E3）
     * 4、类型标识形式（123D）
     * 5、正负数标识形式（+123、-234）
     * </pre>
     *
     * @param str 字符串值
     * @return 是否为数字
     */
    public static boolean isNumber(CharSequence str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        char[] chars = str.toString().toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-' || chars[0] == '+') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && (chars[start + 1] == 'x' || chars[start + 1] == 'X')) {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (false == foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return false == allowSigns && foundDigit;
    }

    /**
     * 判断String是否是整数<br>
     * 支持10进制
     *
     * @param s String
     * @return 是否为整数
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // ------------------------------------------------------------------------------------------- range

    /**
     * 判断字符串是否是Long类型<br>
     * 支持10进制
     *
     * @param s String
     * @return 是否为{@link Long}类型
     * @since 1.0.0
     */
    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否是浮点数
     *
     * @param s String
     * @return 是否为{@link Double}类型
     */
    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return s.contains(".");
        } catch (NumberFormatException ignore) {
            // ignore
        }
        return false;
    }

    /**
     * 是否是质数（素数）<br>
     * 质数表的质数又称素数。指整数在一个大于1的自然数中,除了1和此整数自身外,没法被其他自然数整除的数。
     *
     * @param n 数字
     * @return 是否是质数
     */
    public static boolean isPrimes(int n) {
        Precondition.isTrue(n > 1, "The number must be > 1");
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成不重复随机数 根据给定的最小数字和最大数字，以及随机数的个数，产生指定的不重复的数组
     *
     * @param begin 最小数字（包含该数）
     * @param end   最大数字（不包含该数）
     * @param size  指定产生随机数的个数
     * @return 随机int数组
     */
    public static int[] generateRandomNumber(int begin, int end, int size) {
        // 种子你可以随意生成，但不能重复
        final int[] seed = ArrayUtil.range(begin, end);
        return generateRandomNumber(begin, end, size, seed);
    }

    /**
     * 生成不重复随机数 根据给定的最小数字和最大数字，以及随机数的个数，产生指定的不重复的数组
     *
     * @param begin 最小数字（包含该数）
     * @param end   最大数字（不包含该数）
     * @param size  指定产生随机数的个数
     * @param seed  种子，用于取随机数的int池
     * @return 随机int数组
     * @since 1.0.0
     */
    public static int[] generateRandomNumber(int begin, int end, int size, int[] seed) {
        if (begin > end) {
            int temp = begin;
            begin = end;
            end = temp;
        }
        // 加入逻辑判断，确保begin<end并且size不能大于该表示范围
        Precondition.isTrue((end - begin) >= size, "Size is larger than range between begin and end!");
        Precondition.isTrue(seed.length >= size, "Size is larger than seed size!");

        final int[] ranArr = new int[size];
        // 数量你可以自己定义。
        for (int i = 0; i < size; i++) {
            // 得到一个位置
            int j = RandomUtil.randomInt(seed.length - i);
            // 得到那个位置的数值
            ranArr[i] = seed[j];
            // 将最后一个未用的数字放到这里
            seed[j] = seed[seed.length - 1 - i];
        }
        return ranArr;
    }

    // ------------------------------------------------------------------------------------------- others

    /**
     * 生成不重复随机数 根据给定的最小数字和最大数字，以及随机数的个数，产生指定的不重复的数组
     *
     * @param begin 最小数字（包含该数）
     * @param end   最大数字（不包含该数）
     * @param size  指定产生随机数的个数
     * @return 随机int数组
     */
    public static Integer[] generateBySet(int begin, int end, int size) {
        if (begin > end) {
            int temp = begin;
            begin = end;
            end = temp;
        }
        // 加入逻辑判断，确保begin<end并且size不能大于该表示范围
        if ((end - begin) < size) {
            throw new UtilException("Size is larger than range between begin and end!");
        }

        Random ran = new Random();
        Set<Integer> set = new HashSet<>();
        while (set.size() < size) {
            set.add(begin + ran.nextInt(end - begin));
        }

        return set.toArray(new Integer[size]);
    }

    /**
     * 从0开始给定范围内的整数列表，步进为1
     *
     * @param stop 结束（包含）
     * @return 整数列表
     * @since 1.0.0
     */
    public static int[] range(int stop) {
        return range(0, stop);
    }

    /**
     * 给定范围内的整数列表，步进为1
     *
     * @param start 开始（包含）
     * @param stop  结束（包含）
     * @return 整数列表
     */
    public static int[] range(int start, int stop) {
        return range(start, stop, 1);
    }

    /**
     * 给定范围内的整数列表
     *
     * @param start 开始（包含）
     * @param stop  结束（包含）
     * @param step  步进
     * @return 整数列表
     */
    public static int[] range(int start, int stop, int step) {
        if (start < stop) {
            step = Math.abs(step);
        } else if (start > stop) {
            step = -Math.abs(step);
        } else {// start == end
            return new int[]{start};
        }

        int size = Math.abs((stop - start) / step) + 1;
        int[] values = new int[size];
        int index = 0;
        for (int i = start; (step > 0) ? i <= stop : i >= stop; i += step) {
            values[index] = i;
            index++;
        }
        return values;
    }

    /**
     * 将给定范围内的整数添加到已有集合中，步进为1
     *
     * @param start  开始（包含）
     * @param stop   结束（包含）
     * @param values 集合
     * @return 集合
     */
    public static Collection<Integer> appendRange(int start, int stop, Collection<Integer> values) {
        return appendRange(start, stop, 1, values);
    }

    /**
     * 将给定范围内的整数添加到已有集合中
     *
     * @param start  开始（包含）
     * @param stop   结束（包含）
     * @param step   步进
     * @param values 集合
     * @return 集合
     */
    public static Collection<Integer> appendRange(int start, int stop, int step, Collection<Integer> values) {
        if (start < stop) {
            step = Math.abs(step);
        } else if (start > stop) {
            step = -Math.abs(step);
        } else {// start == end
            values.add(start);
            return values;
        }

        for (int i = start; (step > 0) ? i <= stop : i >= stop; i += step) {
            values.add(i);
        }
        return values;
    }

    /**
     * 计算阶乘
     * <p>
     * n! = n * (n-1) * ... * 2 * 1
     * </p>
     *
     * @param n 阶乘起始
     * @return 结果
     * @since 1.0.0
     */
    public static BigInteger factorial(BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        }
        return factorial(n, BigInteger.ZERO);
    }

    /**
     * 计算范围阶乘
     * <p>
     * factorial(start, end) = start * (start - 1) * ... * (end + 1)
     * </p>
     *
     * @param start 阶乘起始（包含）
     * @param end   阶乘结束，必须小于起始（不包括）
     * @return 结果
     * @since 1.0.0
     */
    public static BigInteger factorial(BigInteger start, BigInteger end) {
        Precondition.notNull(start, "Factorial start must be not null!");
        Precondition.notNull(end, "Factorial end must be not null!");
        if (start.compareTo(BigInteger.ZERO) < 0 || end.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException(StrUtil.format("Factorial start and end both must be > 0, but got start={}, end={}", start, end));
        }

        if (start.equals(BigInteger.ZERO)) {
            start = BigInteger.ONE;
        }

        if (end.compareTo(BigInteger.ONE) < 0) {
            end = BigInteger.ONE;
        }

        BigInteger result = start;
        end = end.add(BigInteger.ONE);
        while (start.compareTo(end) > 0) {
            start = start.subtract(BigInteger.ONE);
            result = result.multiply(start);
        }
        return result;
    }

    /**
     * 计算范围阶乘
     * <p>
     * factorial(start, end) = start * (start - 1) * ... * (end + 1)
     * </p>
     *
     * @param start 阶乘起始（包含）
     * @param end   阶乘结束，必须小于起始（不包括）
     * @return 结果
     * @since 1.0.0
     */
    public static long factorial(long start, long end) {
        // 负数没有阶乘
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException(StrUtil.format("Factorial start and end both must be >= 0, but got start={}, end={}", start, end));
        }
        if (0L == start || start == end) {
            return 1L;
        }
        if (start < end) {
            return 0L;
        }
        return factorialMultiplyAndCheck(start, factorial(start - 1, end));
    }

    /**
     * 计算范围阶乘中校验中间的计算是否存在溢出，factorial提前做了负数和0的校验，因此这里没有校验数字的正负
     *
     * @param a 乘数
     * @param b 被乘数
     * @return 如果 a * b的结果没有溢出直接返回，否则抛出异常
     */
    private static long factorialMultiplyAndCheck(long a, long b) {
        if (a <= Long.MAX_VALUE / b) {
            return a * b;
        }
        throw new IllegalArgumentException(StrUtil.format("Overflow in multiplication: {} * {}", a, b));
    }

    /**
     * 计算阶乘
     * <p>
     * n! = n * (n-1) * ... * 2 * 1
     * </p>
     *
     * @param n 阶乘起始
     * @return 结果
     */
    public static long factorial(long n) {
        if (n < 0 || n > 20) {
            throw new IllegalArgumentException(StrUtil.format("Factorial must have n >= 0 and n <= 20 for n!, but got n = {}", n));
        }
        return FACTORIALS[(int) n];
    }

    /**
     * 计算阶乘
     * <p>
     * n! = n * (n-1) * ... * 2 * 1
     * </p>
     *
     * @param n 阶乘起始
     * @return 结果
     */
    public static long factorial(int n) {
        return factorial((long) n);
    }

    /**
     * 平方根算法<br>
     * 推荐使用 {@link Math#sqrt(double)}
     *
     * @param x 值
     * @return 平方根
     */
    public static long sqrt(long x) {
        long y = 0;
        long b = (~Long.MAX_VALUE) >>> 1;
        while (b > 0) {
            if (x >= y + b) {
                x -= y + b;
                y >>= 1;
                y += b;
            } else {
                y >>= 1;
            }
            b >>= 2;
        }
        return y;
    }

    // ------------------------------------------------------------------------------------------- compare

    /**
     * 可以用于计算双色球、大乐透注数的方法<br>
     * 比如大乐透35选5可以这样调用processMultiple(7,5); 就是数学中的：C75=7*6/2*1
     *
     * @param selectNum 选中小球个数
     * @param minNum    最少要选中多少个小球
     * @return 注数
     */
    public static int processMultiple(int selectNum, int minNum) {
        int result;
        result = mathSubNode(selectNum, minNum) / mathNode(selectNum - minNum);
        return result;
    }

    /**
     * 最大公约数
     *
     * @param m 第一个值
     * @param n 第二个值
     * @return 最大公约数
     */
    public static int divisor(int m, int n) {
        while (m % n != 0) {
            int temp = m % n;
            m = n;
            n = temp;
        }
        return n;
    }

    /**
     * 最小公倍数
     *
     * @param m 第一个值
     * @param n 第二个值
     * @return 最小公倍数
     */
    public static int multiple(int m, int n) {
        return m * n / divisor(m, n);
    }

    /**
     * 获得数字对应的二进制字符串
     *
     * @param number 数字
     * @return 二进制字符串
     */
    public static String getBinaryStr(Number number) {
        if (number instanceof Long) {
            return Long.toBinaryString((Long) number);
        } else if (number instanceof Integer) {
            return Integer.toBinaryString((Integer) number);
        } else {
            return Long.toBinaryString(number.longValue());
        }
    }

    /**
     * 二进制转int
     *
     * @param binaryStr 二进制字符串
     * @return int
     */
    public static int binaryToInt(String binaryStr) {
        return Integer.parseInt(binaryStr, 2);
    }

    /**
     * 二进制转long
     *
     * @param binaryStr 二进制字符串
     * @return long
     */
    public static long binaryToLong(String binaryStr) {
        return Long.parseLong(binaryStr, 2);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
     * @see Character#compare(char, char)
     * @since 1.0.0
     */
    public static int compare(char x, char y) {
        return x - y;
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
     * @see Double#compare(double, double)
     * @since 1.0.0
     */
    public static int compare(double x, double y) {
        return Double.compare(x, y);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
     * @see Integer#compare(int, int)
     * @since 1.0.0
     */
    public static int compare(int x, int y) {
        return Integer.compare(x, y);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
     * @see Long#compare(long, long)
     * @since 1.0.0
     */
    public static int compare(long x, long y) {
        return Long.compare(x, y);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
     * @see Short#compare(short, short)
     * @since 1.0.0
     */
    public static int compare(short x, short y) {
        return Short.compare(x, y);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
     * @see Byte#compare(byte, byte)
     * @since 1.0.0
     */
    public static int compare(byte x, byte y) {
        return Byte.compare(x, y);
    }

    /**
     * 比较大小，参数1 &gt; 参数2 返回true
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否大于
     * @since 1.0.0
     */
    public static boolean isGreater(BigDecimal bigNum1, BigDecimal bigNum2) {
        Precondition.notNull(bigNum1);
        Precondition.notNull(bigNum2);
        return bigNum1.compareTo(bigNum2) > 0;
    }

    /**
     * 比较大小，参数1 &gt;= 参数2 返回true
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否大于等于
     * @since 1.0.0
     */
    public static boolean isGreaterOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
        Precondition.notNull(bigNum1);
        Precondition.notNull(bigNum2);
        return bigNum1.compareTo(bigNum2) >= 0;
    }

    /**
     * 比较大小，参数1 &lt; 参数2 返回true
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否小于
     * @since 1.0.0
     */
    public static boolean isLess(BigDecimal bigNum1, BigDecimal bigNum2) {
        Precondition.notNull(bigNum1);
        Precondition.notNull(bigNum2);
        return bigNum1.compareTo(bigNum2) < 0;
    }

    /**
     * 比较大小，参数1&lt;=参数2 返回true
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否小于等于
     * @since 1.0.0
     */
    public static boolean isLessOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
        Precondition.notNull(bigNum1);
        Precondition.notNull(bigNum2);
        return bigNum1.compareTo(bigNum2) <= 0;
    }

    /**
     * 比较大小，值相等 返回true<br>
     * 此方法通过调用{@link Double#doubleToLongBits(double)}方法来判断是否相等<br>
     * 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否相等
     * @since 1.0.0
     */
    public static boolean equals(double num1, double num2) {
        return Double.doubleToLongBits(num1) == Double.doubleToLongBits(num2);
    }

    /**
     * 比较大小，值相等 返回true<br>
     * 此方法通过调用{@link Float#floatToIntBits(float)}方法来判断是否相等<br>
     * 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否相等
     * @since 1.0.0
     */
    public static boolean equals(float num1, float num2) {
        return Float.floatToIntBits(num1) == Float.floatToIntBits(num2);
    }

    /**
     * 比较大小，值相等 返回true<br>
     * 此方法通过调用{@link BigDecimal#compareTo(BigDecimal)}方法来判断是否相等<br>
     * 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否相等
     */
    public static boolean equals(BigDecimal bigNum1, BigDecimal bigNum2) {
        //noinspection NumberEquality
        if (bigNum1 == bigNum2) {
            // 如果用户传入同一对象，省略compareTo以提高性能。
            return true;
        }
        if (bigNum1 == null || bigNum2 == null) {
            return false;
        }
        return 0 == bigNum1.compareTo(bigNum2);
    }

    /**
     * 比较两个字符是否相同
     *
     * @param c1         字符1
     * @param c2         字符2
     * @param ignoreCase 是否忽略大小写
     * @return 是否相同
     * @see CharUtil#equals(char, char, boolean)
     * @since 1.0.0
     */
    public static boolean equals(char c1, char c2, boolean ignoreCase) {
        return CharUtil.equals(c1, c2, ignoreCase);
    }

    /**
     * 取最小值
     *
     * @param <T>         元素类型
     * @param numberArray 数字数组
     * @return 最小值
     * @see ArrayUtil#min(Comparable[])
     * @since 1.0.0
     */
    public static <T extends Comparable<? super T>> T min(T[] numberArray) {
        return ArrayUtil.min(numberArray);
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @see ArrayUtil#min(long...)
     * @since 1.0.0
     */
    public static long min(long... numberArray) {
        return ArrayUtil.min(numberArray);
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @see ArrayUtil#min(int...)
     * @since 1.0.0
     */
    public static int min(int... numberArray) {
        return ArrayUtil.min(numberArray);
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @see ArrayUtil#min(short...)
     * @since 1.0.0
     */
    public static short min(short... numberArray) {
        return ArrayUtil.min(numberArray);
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @see ArrayUtil#min(double...)
     * @since 1.0.0
     */
    public static double min(double... numberArray) {
        return ArrayUtil.min(numberArray);
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @see ArrayUtil#min(float...)
     * @since 1.0.0
     */
    public static float min(float... numberArray) {
        return ArrayUtil.min(numberArray);
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @see ArrayUtil#min(Comparable[])
     * @since 1.0.0
     */
    public static BigDecimal min(BigDecimal... numberArray) {
        return ArrayUtil.min(numberArray);
    }

    /**
     * 取最大值
     *
     * @param <T>         元素类型
     * @param numberArray 数字数组
     * @return 最大值
     * @see ArrayUtil#max(Comparable[])
     * @since 1.0.0
     */
    public static <T extends Comparable<? super T>> T max(T[] numberArray) {
        return ArrayUtil.max(numberArray);
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @see ArrayUtil#max(long...)
     * @since 1.0.0
     */
    public static long max(long... numberArray) {
        return ArrayUtil.max(numberArray);
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @see ArrayUtil#max(int...)
     * @since 1.0.0
     */
    public static int max(int... numberArray) {
        return ArrayUtil.max(numberArray);
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @see ArrayUtil#max(short...)
     * @since 1.0.0
     */
    public static short max(short... numberArray) {
        return ArrayUtil.max(numberArray);
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @see ArrayUtil#max(double...)
     * @since 1.0.0
     */
    public static double max(double... numberArray) {
        return ArrayUtil.max(numberArray);
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @see ArrayUtil#max(float...)
     * @since 1.0.0
     */
    public static float max(float... numberArray) {
        return ArrayUtil.max(numberArray);
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @see ArrayUtil#max(Comparable[])
     * @since 1.0.0
     */
    public static BigDecimal max(BigDecimal... numberArray) {
        return ArrayUtil.max(numberArray);
    }

    /**
     * 数字转字符串<br>
     * 调用{@link Number#toString()}，并去除尾小数点儿后多余的0
     *
     * @param number       A Number
     * @param defaultValue 如果number参数为{@code null}，返回此默认值
     * @return A String.
     * @since 1.0.0
     */
    public static String toStr(Number number, String defaultValue) {
        return (null == number) ? defaultValue : toStr(number);
    }

    /**
     * 数字转字符串<br>
     * 调用{@link Number#toString()}或 {@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
     *
     * @param number A Number
     * @return A String.
     */
    public static String toStr(Number number) {
        return toStr(number, true);
    }

    /**
     * 数字转字符串<br>
     * 调用{@link Number#toString()}或 {@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
     *
     * @param number               A Number
     * @param isStripTrailingZeros 是否去除末尾多余0，例如5.0返回5
     * @return A String.
     */
    public static String toStr(Number number, boolean isStripTrailingZeros) {
        Precondition.notNull(number, "Number is null !");

        // BigDecimal单独处理，使用非科学计数法
        if (number instanceof BigDecimal) {
            return toStr((BigDecimal) number, isStripTrailingZeros);
        }

        Precondition.isTrue(isValidNumber(number), "Number is non-finite!");
        // 去掉小数点儿后多余的0
        String string = number.toString();
        if (isStripTrailingZeros) {
            if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
                while (string.endsWith("0")) {
                    string = string.substring(0, string.length() - 1);
                }
                if (string.endsWith(".")) {
                    string = string.substring(0, string.length() - 1);
                }
            }
        }
        return string;
    }

    /**
     * {@link BigDecimal}数字转字符串<br>
     * 调用{@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
     *
     * @param bigDecimal A {@link BigDecimal}
     * @return A String.
     * @since 1.0.0
     */
    public static String toStr(BigDecimal bigDecimal) {
        return toStr(bigDecimal, true);
    }

    /**
     * {@link BigDecimal}数字转字符串<br>
     * 调用{@link BigDecimal#toPlainString()}，可选去除尾小数点儿后多余的0
     *
     * @param bigDecimal           A {@link BigDecimal}
     * @param isStripTrailingZeros 是否去除末尾多余0，例如5.0返回5
     * @return A String.
     * @since 1.0.0
     */
    public static String toStr(BigDecimal bigDecimal, boolean isStripTrailingZeros) {
        Precondition.notNull(bigDecimal, "BigDecimal is null !");
        if (isStripTrailingZeros) {
            bigDecimal = bigDecimal.stripTrailingZeros();
        }
        return bigDecimal.toPlainString();
    }

    /**
     * 数字转{@link BigDecimal}<br>
     * Float、Double等有精度问题，转换为字符串后再转换<br>
     * null转换为0
     *
     * @param number 数字
     * @return {@link BigDecimal}
     * @since 1.0.0
     */
    public static BigDecimal toBigDecimal(Number number) {
        if (null == number) {
            return BigDecimal.ZERO;
        }

        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        } else if (number instanceof Long) {
            return new BigDecimal((Long) number);
        } else if (number instanceof Integer) {
            return new BigDecimal((Integer) number);
        } else if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        }

        // Float、Double等有精度问题，转换为字符串后再转换
        return toBigDecimal(number.toString());
    }

    /**
     * 数字转{@link BigDecimal}<br>
     * null或""或空白符转换为0
     *
     * @param numberStr 数字字符串
     * @return {@link BigDecimal}
     * @since 1.0.0
     */
    public static BigDecimal toBigDecimal(String numberStr) {
        if (StrUtil.isBlank(numberStr)) {
            return BigDecimal.ZERO;
        }

        try {
            // 支持类似于 1,234.55 格式的数字
            final Number number = parseNumber(numberStr);
            if (number instanceof BigDecimal) {
                return (BigDecimal) number;
            } else {
                return new BigDecimal(number.toString());
            }
        } catch (Exception ignore) {
            // 忽略解析错误
        }

        return new BigDecimal(numberStr);
    }

    /**
     * 数字转{@link BigInteger}<br>
     * null转换为0
     *
     * @param number 数字
     * @return {@link BigInteger}
     * @since 1.0.0
     */
    public static BigInteger toBigInteger(Number number) {
        if (null == number) {
            return BigInteger.ZERO;
        }

        if (number instanceof BigInteger) {
            return (BigInteger) number;
        } else if (number instanceof Long) {
            return BigInteger.valueOf((Long) number);
        }

        return toBigInteger(number.longValue());
    }

    /**
     * 数字转{@link BigInteger}<br>
     * null或""或空白符转换为0
     *
     * @param number 数字字符串
     * @return {@link BigInteger}
     * @since 1.0.0
     */
    public static BigInteger toBigInteger(String number) {
        return StrUtil.isBlank(number) ? BigInteger.ZERO : new BigInteger(number);
    }

    /**
     * 计算等份个数
     *
     * @param total 总数
     * @param part  每份的个数
     * @return 分成了几份
     * @since 1.0.0
     */
    public static int count(int total, int part) {
        return (total % part == 0) ? (total / part) : (total / part + 1);
    }

    /**
     * 空转0
     *
     * @param decimal {@link BigDecimal}，可以为{@code null}
     * @return {@link BigDecimal}参数为空时返回0的值
     * @since 1.0.0
     */
    public static BigDecimal null2Zero(BigDecimal decimal) {

        return decimal == null ? BigDecimal.ZERO : decimal;
    }

    /**
     * 如果给定值为0，返回1，否则返回原值
     *
     * @param value 值
     * @return 1或非0值
     * @since 1.0.0
     */
    public static int zero2One(int value) {
        return 0 == value ? 1 : value;
    }

    /**
     * 创建{@link BigInteger}，支持16进制、10进制和8进制，如果传入空白串返回null<br>
     * from Apache icefrog Lang
     *
     * @param str 数字字符串
     * @return {@link BigInteger}
     * @since 1.0.0
     */
    public static BigInteger newBigInteger(String str) {
        str = StrUtil.trimToNull(str);
        if (null == str) {
            return null;
        }

        int pos = 0; // 数字字符串位置
        int radix = 10;
        boolean negate = false; // 负数与否
        if (str.startsWith("-")) {
            negate = true;
            pos = 1;
        }
        if (str.startsWith("0x", pos) || str.startsWith("0X", pos)) {
            // hex
            radix = 16;
            pos += 2;
        } else if (str.startsWith("#", pos)) {
            // alternative hex (allowed by Long/Integer)
            radix = 16;
            pos++;
        } else if (str.startsWith("0", pos) && str.length() > pos + 1) {
            // octal; so long as there are additional digits
            radix = 8;
            pos++;
        } // default is to treat as decimal

        if (pos > 0) {
            str = str.substring(pos);
        }
        final BigInteger value = new BigInteger(str, radix);
        return negate ? value.negate() : value;
    }

    /**
     * 判断两个数字是否相邻，例如1和2相邻，1和3不相邻<br>
     * 判断方法为做差取绝对值判断是否为1
     *
     * @param number1 数字1
     * @param number2 数字2
     * @return 是否相邻
     * @since 1.0.0
     */
    public static boolean isBeside(long number1, long number2) {
        return Math.abs(number1 - number2) == 1;
    }

    /**
     * 判断两个数字是否相邻，例如1和2相邻，1和3不相邻<br>
     * 判断方法为做差取绝对值判断是否为1
     *
     * @param number1 数字1
     * @param number2 数字2
     * @return 是否相邻
     * @since 1.0.0
     */
    public static boolean isBeside(int number1, int number2) {
        return Math.abs(number1 - number2) == 1;
    }

    /**
     * 把给定的总数平均分成N份，返回每份的个数<br>
     * 当除以分数有余数时每份+1
     *
     * @param total     总数
     * @param partCount 份数
     * @return 每份的个数
     * @since 1.0.0
     */
    public static int partValue(int total, int partCount) {
        return partValue(total, partCount, true);
    }

    /**
     * 把给定的总数平均分成N份，返回每份的个数<br>
     * 如果isPlusOneWhenHasRem为true，则当除以分数有余数时每份+1，否则丢弃余数部分
     *
     * @param total               总数
     * @param partCount           份数
     * @param isPlusOneWhenHasRem 在有余数时是否每份+1
     * @return 每份的个数
     * @since 1.0.0
     */
    public static int partValue(int total, int partCount, boolean isPlusOneWhenHasRem) {
        int partValue = total / partCount;
        if (isPlusOneWhenHasRem && total % partCount > 0) {
            partValue++;
        }
        return partValue;
    }

    /**
     * 提供精确的幂运算
     *
     * @param number 底数
     * @param n      指数
     * @return 幂的积
     * @since 1.0.0
     */
    public static BigDecimal pow(Number number, int n) {
        return pow(toBigDecimal(number), n);
    }

    /**
     * 提供精确的幂运算
     *
     * @param number 底数
     * @param n      指数
     * @return 幂的积
     * @since 1.0.0
     */
    public static BigDecimal pow(BigDecimal number, int n) {
        return number.pow(n);
    }

    /**
     * 判断一个整数是否是2的幂
     *
     * @param n 待验证的整数
     * @return 如果n是2的幂返回true, 反之返回false
     */
    public static boolean isPowerOfTwo(long n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }

    /**
     * 解析转换数字字符串为int型数字，规则如下：
     *
     * <pre>
     * 1、0x开头的视为16进制数字
     * 2、0开头的忽略开头的0
     * 3、其它情况按照10进制转换
     * 4、空串返回0
     * 5、.123形式返回0（按照小于0的小数对待）
     * 6、123.56截取小数点之前的数字，忽略小数部分
     * </pre>
     *
     * @param number 数字，支持0x开头、0开头和普通十进制
     * @return int
     * @throws NumberFormatException 数字格式异常
     * @since 1.0.0
     */
    public static int parseInt(String number) throws NumberFormatException {
        if (StrUtil.isBlank(number)) {
            return 0;
        }

        if (StrUtil.startWithIgnoreCase(number, "0x")) {
            // 0x04表示16进制数
            return Integer.parseInt(number.substring(2), 16);
        }

        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return parseNumber(number).intValue();
        }
    }

    /**
     * 解析转换数字字符串为long型数字，规则如下：
     *
     * <pre>
     * 1、0x开头的视为16进制数字
     * 2、0开头的忽略开头的0
     * 3、空串返回0
     * 4、其它情况按照10进制转换
     * 5、.123形式返回0（按照小于0的小数对待）
     * 6、123.56截取小数点之前的数字，忽略小数部分
     * </pre>
     *
     * @param number 数字，支持0x开头、0开头和普通十进制
     * @return long
     * @since 1.0.0
     */
    public static long parseLong(String number) {
        if (StrUtil.isBlank(number)) {
            return 0L;
        }

        if (number.startsWith("0x")) {
            // 0x04表示16进制数
            return Long.parseLong(number.substring(2), 16);
        }

        try {
            return Long.parseLong(number);
        } catch (NumberFormatException e) {
            return parseNumber(number).longValue();
        }
    }

    /**
     * 解析转换数字字符串为long型数字，规则如下：
     *
     * <pre>
     * 1、0开头的忽略开头的0
     * 2、空串返回0
     * 3、其它情况按照10进制转换
     * 4、.123形式返回0.123（按照小于0的小数对待）
     * </pre>
     *
     * @param number 数字，支持0x开头、0开头和普通十进制
     * @return long
     * @since 1.0.0
     */
    public static float parseFloat(String number) {
        if (StrUtil.isBlank(number)) {
            return 0f;
        }

        try {
            return Float.parseFloat(number);
        } catch (NumberFormatException e) {
            return parseNumber(number).floatValue();
        }
    }

    /**
     * 解析转换数字字符串为long型数字，规则如下：
     *
     * <pre>
     * 1、0开头的忽略开头的0
     * 2、空串返回0
     * 3、其它情况按照10进制转换
     * 4、.123形式返回0.123（按照小于0的小数对待）
     * </pre>
     *
     * @param number 数字，支持0x开头、0开头和普通十进制
     * @return long
     * @since 1.0.0
     */
    public static double parseDouble(String number) {
        if (StrUtil.isBlank(number)) {
            return 0D;
        }

        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return parseNumber(number).doubleValue();
        }
    }

    /**
     * 将指定字符串转换为{@link Number} 对象
     *
     * @param numberStr Number字符串
     * @return Number对象
     * @throws NumberFormatException 包装了{@link ParseException}，当给定的数字字符串无法解析时抛出
     * @since 1.0.0
     */
    public static Number parseNumber(String numberStr) throws NumberFormatException {
        try {
            final NumberFormat format = NumberFormat.getInstance();
            if (format instanceof DecimalFormat) {
                // 当字符串数字超出double的长度时，会导致截断，此处使用BigDecimal接收
                ((DecimalFormat) format).setParseBigDecimal(true);
            }
            return format.parse(numberStr);
        } catch (ParseException e) {
            final NumberFormatException nfe = new NumberFormatException(e.getMessage());
            nfe.initCause(e);
            throw nfe;
        }

    }

    /**
     * int值转byte数组，使用大端字节序（高位字节在前，低位字节在后）<br>
     * 见：http://www.ruanyifeng.com/blog/2016/11/byte-order.html
     *
     * @param value 值
     * @return byte数组
     * @since 1.0.0
     */
    public static byte[] toBytes(int value) {
        final byte[] result = new byte[4];

        result[0] = (byte) (value >> 24);
        result[1] = (byte) (value >> 16);
        result[2] = (byte) (value >> 8);
        result[3] = (byte) (value /* >> 0 */);

        return result;
    }

    /**
     * byte数组转int，使用大端字节序（高位字节在前，低位字节在后）<br>
     * 见：http://www.ruanyifeng.com/blog/2016/11/byte-order.html
     *
     * @param bytes byte数组
     * @return int
     * @since 1.0.0
     */
    public static int toInt(byte[] bytes) {
        return (bytes[0] & 0xff) << 24//
                | (bytes[1] & 0xff) << 16//
                | (bytes[2] & 0xff) << 8//
                | (bytes[3] & 0xff);
    }

    /**
     * 以无符号字节数组的形式返回传入值。
     *
     * @param value 需要转换的值
     * @return 无符号bytes
     * @since 1.0.0
     */
    public static byte[] toUnsignedByteArray(BigInteger value) {
        byte[] bytes = value.toByteArray();

        if (bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);

            return tmp;
        }

        return bytes;
    }

    /**
     * 以无符号字节数组的形式返回传入值。
     *
     * @param length bytes长度
     * @param value  需要转换的值
     * @return 无符号bytes
     * @since 1.0.0
     */
    public static byte[] toUnsignedByteArray(int length, BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length == length) {
            return bytes;
        }

        int start = bytes[0] == 0 ? 1 : 0;
        int count = bytes.length - start;

        if (count > length) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }

        byte[] tmp = new byte[length];
        System.arraycopy(bytes, start, tmp, tmp.length - count, count);
        return tmp;
    }

    /**
     * 无符号bytes转{@link BigInteger}
     *
     * @param buf buf 无符号bytes
     * @return {@link BigInteger}
     * @since 1.0.0
     */
    public static BigInteger fromUnsignedByteArray(byte[] buf) {
        return new BigInteger(1, buf);
    }

    /**
     * 无符号bytes转{@link BigInteger}
     *
     * @param buf    无符号bytes
     * @param off    起始位置
     * @param length 长度
     * @return {@link BigInteger}
     */
    public static BigInteger fromUnsignedByteArray(byte[] buf, int off, int length) {
        byte[] mag = buf;
        if (off != 0 || length != buf.length) {
            mag = new byte[length];
            System.arraycopy(buf, off, mag, 0, length);
        }
        return new BigInteger(1, mag);
    }

    /**
     * 检查是否为有效的数字<br>
     * 检查Double和Float是否为无限大，或者Not a Number<br>
     * 非数字类型和Null将返回true
     *
     * @param number 被检查类型
     * @return 检查结果，非数字类型和Null将返回true
     * @since 1.0.0
     */
    public static boolean isValidNumber(Number number) {
        if (number instanceof Double) {
            return (false == ((Double) number).isInfinite()) && (false == ((Double) number).isNaN());
        } else if (number instanceof Float) {
            return (false == ((Float) number).isInfinite()) && (false == ((Float) number).isNaN());
        }
        return true;
    }

    /**
     * 检查是否为有效的数字<br>
     * 检查double否为无限大，或者Not a Number（NaN）<br>
     *
     * @param number 被检查double
     * @return 检查结果
     * @since 1.0.0
     */
    public static boolean isValid(double number) {
        return false == (Double.isNaN(number) || Double.isInfinite(number));
    }

    /**
     * 检查是否为有效的数字<br>
     * 检查double否为无限大，或者Not a Number（NaN）<br>
     *
     * @param number 被检查double
     * @return 检查结果
     * @since 1.0.0
     */
    public static boolean isValid(float number) {
        return false == (Float.isNaN(number) || Float.isInfinite(number));
    }

    /**
     * 计算数学表达式的值，只支持加减乘除和取余<br>
     * 如：
     * <pre class="code">
     *   calculate("(0*1--3)-5/-4-(3*(-2.13))") -》 10.64
     * </pre>
     *
     * @param expression 数学表达式
     * @return 结果
     * @since 1.0.0
     */
    public static double calculate(String expression) {
        return Calculator.conversion(expression);
    }

    /**
     * Number值转换为double<br>
     * float强制转换存在精度问题，此方法避免精度丢失
     *
     * @param value 被转换的float值
     * @return double值
     * @since 1.0.0
     */
    public static double toDouble(Number value) {
        if (value instanceof Float) {
            return Double.parseDouble(value.toString());
        } else {
            return value.doubleValue();
        }
    }

    /**
     * Convert the given number into an instance of the given target class.
     * <p>
     * 将指定Number 及子类 转换为 具体的类型对象 {@link Number} 对象
     *
     * @param number      the number to convert
     * @param targetClass the target class to convert to
     * @param <T>         T
     * @return the converted number
     * @throws IllegalArgumentException if the target class is not supported
     *                                  (i.e. not a standard Number subclass as included in the JDK)
     * @see Byte
     * @see Short
     * @see Integer
     * @see Long
     * @see BigInteger
     * @see Float
     * @see Double
     * @see BigDecimal
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T convertNumberToTargetClass(Number number, Class<T> targetClass)
            throws IllegalArgumentException {

        Precondition.notNull(number, "Number must not be null");
        Precondition.notNull(targetClass, "Target class must not be null");

        if (targetClass.isInstance(number)) {
            return (T) number;
        } else if (Byte.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Byte.valueOf(number.byteValue());
        } else if (Short.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Short.valueOf(number.shortValue());
        } else if (Integer.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Integer.valueOf(number.intValue());
        } else if (Long.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            return (T) Long.valueOf(value);
        } else if (BigInteger.class == targetClass) {
            if (number instanceof BigDecimal) {
                // do not lose precision - use BigDecimal's own conversion
                return (T) ((BigDecimal) number).toBigInteger();
            } else {
                // original value is not a Big* number - use standard long conversion
                return (T) BigInteger.valueOf(number.longValue());
            }
        } else if (Float.class == targetClass) {
            return (T) Float.valueOf(number.floatValue());
        } else if (Double.class == targetClass) {
            return (T) Double.valueOf(number.doubleValue());
        } else if (BigDecimal.class == targetClass) {
            // always use BigDecimal(String) here to avoid unpredictability of BigDecimal(double)
            // (see BigDecimal javadoc for details)
            return (T) new BigDecimal(number.toString());
        } else {
            throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
                    number.getClass().getName() + "] to unsupported target class [" + targetClass.getName() + "]");
        }
    }

    /**
     * Check for a {@code BigInteger}/{@code BigDecimal} long overflow
     * before returning the given number as a long value.
     *
     * @param number      the number to convert
     * @param targetClass the target class to convert to
     * @return the long value, if convertible without overflow
     * @throws IllegalArgumentException if there is an overflow
     * @see #raiseOverflowException
     */
    private static long checkedLongValue(Number number, Class<? extends Number> targetClass) {
        BigInteger bigInt = null;
        if (number instanceof BigInteger) {
            bigInt = (BigInteger) number;
        } else if (number instanceof BigDecimal) {
            bigInt = ((BigDecimal) number).toBigInteger();
        }
        // Effectively analogous to JDK 8's BigInteger.longValueExact()
        if (bigInt != null && (bigInt.compareTo(LONG_MIN) < 0 || bigInt.compareTo(LONG_MAX) > 0)) {
            raiseOverflowException(number, targetClass);
        }
        return number.longValue();
    }

    /**
     * Raise an <em>overflow</em> exception for the given number and target class.
     *
     * @param number      the number we tried to convert
     * @param targetClass the target class we tried to convert to
     * @throws IllegalArgumentException if there is an overflow
     */
    private static void raiseOverflowException(Number number, Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
                number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }

    /**
     * Parse the given {@code text} into a {@link Number} instance of the given
     * target class, using the corresponding {@code decode} / {@code valueOf} method.
     * <p>Trims all whitespace (leading, trailing, and in between characters) from
     * the input {@code String} before attempting to parse the number.
     * <p>Supports numbers in hex format (with leading "0x", "0X", or "#") as well.
     *
     * @param text        the text to convert
     * @param targetClass the target class to parse into
     * @param <T>         T
     * @return the parsed number
     * @throws IllegalArgumentException if the target class is not supported
     *                                  (i.e. not a standard Number subclass as included in the JDK)
     * @see Byte#decode
     * @see Short#decode
     * @see Integer#decode
     * @see Long#decode
     * @see #decodeBigInteger(String)
     * @see Float#valueOf
     * @see Double#valueOf
     * @see BigDecimal#BigDecimal(String)
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
        Precondition.notNull(text, "Text must not be null");
        Precondition.notNull(targetClass, "Target class must not be null");
        String trimmed = StrUtil.trimAllWhitespace(text);

        if (Byte.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
        } else if (Short.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
        } else if (Integer.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
        } else if (Long.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
        } else if (BigInteger.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
        } else if (Float.class == targetClass) {
            return (T) Float.valueOf(trimmed);
        } else if (Double.class == targetClass) {
            return (T) Double.valueOf(trimmed);
        } else if (BigDecimal.class == targetClass || Number.class == targetClass) {
            return (T) new BigDecimal(trimmed);
        } else {
            throw new IllegalArgumentException(
                    "Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
        }
    }

    /**
     * Parse the given {@code text} into a {@link Number} instance of the
     * given target class, using the supplied {@link NumberFormat}.
     * <p>Trims the input {@code String} before attempting to parse the number.
     *
     * @param text         the text to convert
     * @param targetClass  the target class to parse into
     * @param numberFormat the {@code NumberFormat} to use for parsing (if
     *                     {@code null}, this method falls back to {@link #parseNumber(String, Class)})
     * @param <T>          T
     * @return the parsed number
     * @throws IllegalArgumentException if the target class is not supported
     *                                  (i.e. not a standard Number subclass as included in the JDK)
     * @see NumberFormat#parse
     * @see #convertNumberToTargetClass
     * @see #parseNumber(String, Class)
     */
    public static <T extends Number> T parseNumber(
            String text, Class<T> targetClass, NumberFormat numberFormat) {

        if (numberFormat != null) {
            Precondition.notNull(text, "Text must not be null");
            Precondition.notNull(targetClass, "Target class must not be null");
            DecimalFormat decimalFormat = null;
            boolean resetBigDecimal = false;
            if (numberFormat instanceof DecimalFormat) {
                decimalFormat = (DecimalFormat) numberFormat;
                if (BigDecimal.class == targetClass && !decimalFormat.isParseBigDecimal()) {
                    decimalFormat.setParseBigDecimal(true);
                    resetBigDecimal = true;
                }
            }
            try {
                Number number = numberFormat.parse(StrUtil.trimAllWhitespace(text));
                return convertNumberToTargetClass(number, targetClass);
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Could not parse number: " + ex.getMessage());
            } finally {
                if (resetBigDecimal) {
                    decimalFormat.setParseBigDecimal(false);
                }
            }
        } else {
            return parseNumber(text, targetClass);
        }
    }

    /**
     * Determine whether the given {@code value} String indicates a hex number,
     * i.e. needs to be passed into {@code Integer.decode} instead of
     * {@code Integer.valueOf}, etc.
     */
    private static boolean isHexNumber(String value) {
        int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }

    /**
     * Decode a {@link BigInteger} from the supplied {@link String} value.
     * <p>Supports decimal, hex, and octal notation.
     *
     * @see BigInteger#BigInteger(String, int)
     */
    private static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;

        // Handle minus sign, if present.
        if (value.startsWith("-")) {
            negative = true;
            index++;
        }

        // Handle radix specifier, if present.
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }

        BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate() : result);
    }

    /**
     * Returns the {@code int} nearest in value to {@code value}.
     * 返回该值的邻近值
     *
     * @param castClass 传入的数据类型值   以该类型值的最大最小作为比较
     * @param value     any {@code long} value
     * @return the same value cast to {@code int} if it is in the range of the {@code int} type,
     * {@link Integer#MAX_VALUE} if it is too large, or {@link Integer#MIN_VALUE} if it is too
     * small
     */
    public static long saturatedCast(long value, Class<?> castClass) {
        Precondition.checkNotNull(castClass);
        if (Byte.class == castClass) {
            if (value > Byte.MAX_VALUE) {
                return Byte.MAX_VALUE;
            }
            if (value < Byte.MIN_VALUE) {
                return Byte.MIN_VALUE;
            }
            return (byte) value;
        } else if (Short.class == castClass) {
            if (value > Short.MAX_VALUE) {
                return Short.MAX_VALUE;
            }
            if (value < Short.MIN_VALUE) {
                return Short.MIN_VALUE;
            }
            return (short) value;

        } else if (Integer.class == castClass) {

            if (value > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            if (value < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            return (int) value;
        } else if (Character.class == castClass) {
            if (value > Character.MAX_VALUE) {
                return Character.MAX_VALUE;
            }
            if (value < Character.MIN_VALUE) {
                return Character.MIN_VALUE;
            }
            return (char) value;

        } else if (Long.class == castClass) {
            if (value > Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            if (value < Long.MIN_VALUE) {
                return Long.MIN_VALUE;
            }
            return (long) value;

        } else {
            throw new IllegalArgumentException(
                    "Cannot saturatedCast long [" + value + "] to target class [" + castClass.getName() + "]");
        }

    }

    /**
     * Returns the sum of {@code a} and {@code b} unless it would overflow or underflow in which case
     * {@code Long.MAX_VALUE} or {@code Long.MIN_VALUE} is returned, respectively.
     */
    public static long saturatedAdd(long a, long b) {
        long naiveSum = a + b;
        if ((a ^ b) < 0 | (a ^ naiveSum) >= 0) {
            // If a and b have different signs or a has the same sign as the result then there was no
            // overflow, return.
            return naiveSum;
        }
        // we did over/under flow, if the sign is negative we should return MAX otherwise MIN
        return Long.MAX_VALUE + ((naiveSum >>> (Long.SIZE - 1)) ^ 1);
    }

    public static int saturatedMultiply(int a, int b) {
        return (int) saturatedCast((long) a * b, Integer.class);
    }

    /**
     * Returns {@code n} choose {@code k}, also known as the binomial coefficient of {@code n} and
     * {@code k}, or {@link Integer#MAX_VALUE} if the result does not fit in an {@code int}.
     *
     * @throws IllegalArgumentException if {@code n < 0}, {@code k < 0} or {@code k > n}
     */
    public static int binomial(int n, int k) {
        checkNonNegative("n", n);
        checkNonNegative("k", k);
        checkArgument(k <= n, "k (%s) > n (%s)", k, n);
        if (k > (n >> 1)) {
            k = n - k;
        }
        if (k >= biggestBinomials.length || n > biggestBinomials[k]) {
            return Integer.MAX_VALUE;
        }
        switch (k) {
            case 0:
                return 1;
            case 1:
                return n;
            default:
                long result = 1;
                for (int i = 0; i < k; i++) {
                    result *= n - i;
                    result /= i + 1;
                }
                return (int) result;
        }
    }


    /**
     * Returns the value nearest to {@code value} which is within the closed range {@code [min..max]}.
     *
     * <p>If {@code value} is within the range {@code [min..max]}, {@code value} is returned
     * unchanged. If {@code value} is less than {@code min}, {@code min} is returned, and if {@code
     * value} is greater than {@code max}, {@code max} is returned.
     *
     * @param value the {@code int} value to constrain
     * @param min   the lower bound (inclusive) of the range to constrain {@code value} to
     * @param max   the upper bound (inclusive) of the range to constrain {@code value} to
     * @throws IllegalArgumentException if {@code min > max}
     */

    public static int constrainToRange(int value, int min, int max) {
        checkArgument(min <= max, "min (%s) must be less than or equal to max (%s)", min, max);
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Returns the sum of {@code a} and {@code b}, provided it does not overflow.
     *
     * @throws ArithmeticException if {@code a + b} overflows in signed {@code int} arithmetic
     */
    public static int checkedAdd(int a, int b) {
        long result = (long) a + b;
        checkNoOverflow(result == (int) result, "checkedAdd", a, b);
        return (int) result;
    }

    /**
     * Returns the {@code int} value that is equal to {@code value}, if possible.
     *
     * @param value any value in the range of the {@code int} type
     * @return the {@code int} value that equals {@code value}
     * @throws IllegalArgumentException if {@code value} is greater than {@link Integer#MAX_VALUE} or
     *                                  less than {@link Integer#MIN_VALUE}
     */
    public static int checkedCast(long value) {
        int result = (int) value;
        checkArgument(result == value, "Out of range: %s", value);
        return result;
    }


    // ------------------------------------------------------------------------------------------- Private method start
    private static int mathSubNode(int selectNum, int minNum) {
        if (selectNum == minNum) {
            return 1;
        } else {
            return selectNum * mathSubNode(selectNum - 1, minNum);
        }
    }

    private static int mathNode(int selectNum) {
        if (selectNum == 0) {
            return 1;
        } else {
            return selectNum * mathNode(selectNum - 1);
        }
    }

    // ------------------------------------------------------------------------------------------- Private method end


    /**
     * Returns the square root of {@code x}, rounded with the specified rounding mode.
     *
     * @throws IllegalArgumentException if {@code x < 0}
     * @throws ArithmeticException      if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code
     *                                  sqrt(x)} is not an integer
     */
    @SuppressWarnings("fallthrough")
    public static int sqrt(int x, RoundingMode mode) {
        checkNonNegative("x", x);
        int sqrtFloor = sqrtFloor(x);
        switch (mode) {
            case UNNECESSARY:
                checkRoundingUnnecessary(sqrtFloor * sqrtFloor == x); // fall through
            case FLOOR:
            case DOWN:
                return sqrtFloor;
            case CEILING:
            case UP:
                return sqrtFloor + lessThanBranchFree(sqrtFloor * sqrtFloor, x);
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                int halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
                /*
                 * We wish to test whether or not x <= (sqrtFloor + 0.5)^2 = halfSquare + 0.25. Since both x
                 * and halfSquare are integers, this is equivalent to testing whether or not x <=
                 * halfSquare. (We have to deal with overflow, though.)
                 *
                 * If we treat halfSquare as an unsigned int, we know that
                 *            sqrtFloor^2 <= x < (sqrtFloor + 1)^2
                 * halfSquare - sqrtFloor <= x < halfSquare + sqrtFloor + 1
                 * so |x - halfSquare| <= sqrtFloor.  Therefore, it's safe to treat x - halfSquare as a
                 * signed int, so lessThanBranchFree is safe for use.
                 */
                return sqrtFloor + lessThanBranchFree(halfSquare, x);
            default:
                throw new AssertionError();
        }
    }

    private static int sqrtFloor(int x) {
        // There is no loss of precision in converting an int to a double, according to
        // http://java.sun.com/docs/books/jls/third_edition/html/conversions.html#5.1.2
        return (int) Math.sqrt(x);
    }

    public static boolean fitsInInt(long x) {
        return (int) x == x;
    }

    @SuppressWarnings("fallthrough")
    public static long sqrt(long x, RoundingMode mode) {
        checkNonNegative("x", x);
        if (fitsInInt(x)) {
            return sqrt((int) x, mode);
        }
        /*
         * Let k be the true value of floor(sqrt(x)), so that
         *
         *            k * k <= x          <  (k + 1) * (k + 1)
         * (double) (k * k) <= (double) x <= (double) ((k + 1) * (k + 1))
         *          since casting to double is nondecreasing.
         *          Note that the right-hand inequality is no longer strict.
         * Math.sqrt(k * k) <= Math.sqrt(x) <= Math.sqrt((k + 1) * (k + 1))
         *          since Math.sqrt is monotonic.
         * (long) Math.sqrt(k * k) <= (long) Math.sqrt(x) <= (long) Math.sqrt((k + 1) * (k + 1))
         *          since casting to long is monotonic
         * k <= (long) Math.sqrt(x) <= k + 1
         *          since (long) Math.sqrt(k * k) == k, as checked exhaustively in
         *          {@link LongMathTest#testSqrtOfPerfectSquareAsDoubleIsPerfect}
         */
        long guess = (long) Math.sqrt(x);
        // Note: guess is always <= FLOOR_SQRT_MAX_LONG.
        long guessSquared = guess * guess;
        // Note (2013-2-26): benchmarks indicate that, inscrutably enough, using if statements is
        // faster here than using lessThanBranchFree.
        switch (mode) {
            case UNNECESSARY:
                checkRoundingUnnecessary(guessSquared == x);
                return guess;
            case FLOOR:
            case DOWN:
                if (x < guessSquared) {
                    return guess - 1;
                }
                return guess;
            case CEILING:
            case UP:
                if (x > guessSquared) {
                    return guess + 1;
                }
                return guess;
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                long sqrtFloor = guess - ((x < guessSquared) ? 1 : 0);
                long halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
                /*
                 * We wish to test whether or not x <= (sqrtFloor + 0.5)^2 = halfSquare + 0.25. Since both x
                 * and halfSquare are integers, this is equivalent to testing whether or not x <=
                 * halfSquare. (We have to deal with overflow, though.)
                 *
                 * If we treat halfSquare as an unsigned long, we know that
                 *            sqrtFloor^2 <= x < (sqrtFloor + 1)^2
                 * halfSquare - sqrtFloor <= x < halfSquare + sqrtFloor + 1
                 * so |x - halfSquare| <= sqrtFloor.  Therefore, it's safe to treat x - halfSquare as a
                 * signed long, so lessThanBranchFree is safe for use.
                 */
                return sqrtFloor + lessThanBranchFree(halfSquare, x);
            default:
                throw new AssertionError();
        }
    }

    /**
     * 百分比
     *
     * @param p1 分子
     * @param p2 分母
     * @return 百分比
     */
    public static int percentDiff(int p1, int p2) {
        /*  19 */
        if (p1 == 0 && p2 == 0) {
            return 0;
        }
        /*  20 */
        double bottom = (p1 + p2) / 2.0D;
        /*  21 */
        double val = (p1 - p2) / bottom * 100.0D;
        /*  22 */
        return (int) ((val < 0.0D) ? Math.ceil(val * -1.0D) : Math.ceil(val));
    }


}
