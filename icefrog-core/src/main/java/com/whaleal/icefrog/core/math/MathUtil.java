package com.whaleal.icefrog.core.math;

import java.math.RoundingMode;
import java.util.List;

import static com.whaleal.icefrog.core.lang.Preconditions.checkPositive;
import static com.whaleal.icefrog.core.util.NumberUtil.isPowerOfTwo;

/**
 * 数学相关方法工具类<br>
 * 此工具类与{@link com.whaleal.icefrog.core.util.NumberUtil}属于一类工具，NumberUtil偏向于简单数学计算的封装，MathUtil偏向复杂数学计算
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class MathUtil {

	//--------------------------------------------------------------------------------------------- Arrangement
	/**
	 * 计算排列数，即A(n, m) = n!/(n-m)!
	 *
	 * @param n 总数
	 * @param m 选择的个数
	 * @return 排列数
	 */
	public static long arrangementCount(int n, int m) {
		return Arrangement.count(n, m);
	}

	/**
	 * 计算排列数，即A(n, n) = n!
	 *
	 * @param n 总数
	 * @return 排列数
	 */
	public static long arrangementCount(int n) {
		return Arrangement.count(n);
	}

	/**
	 * 排列选择（从列表中选择n个排列）
	 *
	 * @param datas 待选列表
	 * @param m 选择个数
	 * @return 所有排列列表
	 */
	public static List<String[]> arrangementSelect(String[] datas, int m) {
		return new Arrangement(datas).select(m);
	}

	/**
	 * 全排列选择（列表全部参与排列）
	 *
	 * @param datas 待选列表
	 * @return 所有排列列表
	 */
	public static List<String[]> arrangementSelect(String[] datas) {
		return new Arrangement(datas).select();
	}

	//--------------------------------------------------------------------------------------------- Combination
	/**
	 * 计算组合数，即C(n, m) = n!/((n-m)! * m!)
	 *
	 * @param n 总数
	 * @param m 选择的个数
	 * @return 组合数
	 */
	public static long combinationCount(int n, int m) {
		return Combination.count(n, m);
	}

	/**
	 * 组合选择（从列表中选择n个组合）
	 *
	 * @param datas 待选列表
	 * @param m 选择个数
	 * @return 所有组合列表
	 */
	public static List<String[]> combinationSelect(String[] datas, int m) {
		return new Combination(datas).select(m);
	}

	/**
	 * 金额元转换为分
	 *
	 * @param yuan 金额，单位元
	 * @return 金额，单位分
	 * @since 1.0.0
	 */
	public static long yuanToCent(double yuan) {
		return new Money(yuan).getCent();
	}

	/**
	 * 金额分转换为元
	 *
	 * @param cent 金额，单位分
	 * @return 金额，单位元
	 * @since 1.0.0
	 */
	public static double centToYuan(long cent) {
		long yuan = cent / 100;
		int centPart = (int) (cent % 100);
		return new Money(yuan, centPart).getAmount().doubleValue();
	}

	/**
	 * Returns the product of {@code a} and {@code b}, provided it does not overflow.
	 *
	 * @throws ArithmeticException if {@code a * b} overflows in signed {@code int} arithmetic
	 */
	public static int checkedMultiply(int a, int b) {
		long result = (long) a * b;
		checkNoOverflow(result == (int) result, "checkedMultiply", a, b);
		return (int) result;
	}

	/**
	 * Returns the product of {@code a} and {@code b}, provided it does not overflow.
	 *
	 * @throws ArithmeticException if {@code a * b} overflows in signed {@code long} arithmetic
	 */
	public static long checkedMultiply(long a, long b) {
		// Hacker's Delight, Section 2-12
		int leadingZeros =
				Long.numberOfLeadingZeros(a)
						+ Long.numberOfLeadingZeros(~a)
						+ Long.numberOfLeadingZeros(b)
						+ Long.numberOfLeadingZeros(~b);
		/*
		 * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's < Long.SIZE it's definitely
		 * bad. We do the leadingZeros check to avoid the division below if at all possible.
		 *
		 * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a are 0 and 1. We take
		 * care of all a < 0 with their own check, because in particular, the case a == -1 will
		 * incorrectly pass the division check below.
		 *
		 * In all other cases, we check that either a is 0 or the result is consistent with division.
		 */
		if (leadingZeros > Long.SIZE + 1) {
			return a * b;
		}
		checkNoOverflow(leadingZeros >= Long.SIZE, "checkedMultiply", a, b);
		checkNoOverflow(a >= 0 | b != Long.MIN_VALUE, "checkedMultiply", a, b);
		long result = a * b;
		checkNoOverflow(a == 0 || result / a == b, "checkedMultiply", a, b);
		return result;
	}

	static void checkNoOverflow(boolean condition, String methodName, int a, int b) {
		if (!condition) {
			throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")");
		}
	}

	static void checkNoOverflow(boolean condition, String methodName, long a, long b) {
		if (!condition) {
			throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")");
		}
	}

	/**
	 * Returns the base-2 logarithm of {@code x}, rounded according to the specified rounding mode.
	 *
	 * @throws IllegalArgumentException if {@code x <= 0}
	 * @throws ArithmeticException if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code x}
	 *     is not a power of two
	 */
	@SuppressWarnings("fallthrough")
	// TODO(kevinb): remove after this warning is disabled globally
	public static int log2(int x, RoundingMode mode) {
		checkPositive("x", x);
		switch (mode) {
			case UNNECESSARY:
				checkRoundingUnnecessary(isPowerOfTwo(x));
				// fall through
			case DOWN:
			case FLOOR:
				return (Integer.SIZE - 1) - Integer.numberOfLeadingZeros(x);

			case UP:
			case CEILING:
				return Integer.SIZE - Integer.numberOfLeadingZeros(x - 1);

			case HALF_DOWN:
			case HALF_UP:
			case HALF_EVEN:
				// Since sqrt(2) is irrational, log2(x) - logFloor cannot be exactly 0.5
				int leadingZeros = Integer.numberOfLeadingZeros(x);
				int cmp = MAX_POWER_OF_SQRT2_UNSIGNED >>> leadingZeros;
				// floor(2^(logFloor + 0.5))
				int logFloor = (Integer.SIZE - 1) - leadingZeros;
				return logFloor + lessThanBranchFree(cmp, x);

			default:
				throw new AssertionError();
		}
	}
	static int lessThanBranchFree(int x, int y) {
		// The double negation is optimized away by normal Java, but is necessary for GWT
		// to make sure bit twiddling works as expected.
		return ~~(x - y) >>> (Integer.SIZE - 1);
	}
	static void checkRoundingUnnecessary(boolean condition) {
		if (!condition) {
			throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
		}
	}

	static final int MAX_POWER_OF_SQRT2_UNSIGNED = 0xB504F333;




}
