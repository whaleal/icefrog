package com.whaleal.icefrog.core.codec;


import com.whaleal.icefrog.core.util.CharUtil;
import com.whaleal.icefrog.core.util.CharsetUtil;
import com.whaleal.icefrog.core.util.StrUtil;

import java.awt.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Converts hexadecimal Strings. The Charset used for certain operation can be set, the default is set in Charset
 * <p>
 * This class is thread-safe.
 *
 * @see com.whaleal.icefrog.core.util.HexUtil  本质与调用 HexUtil 相同
 * @see Charset
 *
 * @author wh
 * @since 1.0
 */
public class Hex {


	/**
	 * Used to build output as hex.
	 * 用于建立十六进制字符的输出的小写字符数组
	 */
	private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f'};
	/**
	 * Used to build output as hex.
	 * 用于建立十六进制字符的输出的大写字符数组
	 */
	private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F'};



	/**
	 * 将一个 16进制的 char[] 数组进行解码 转为字节数组
	 * 将十六进制字符数组转换为字节数组
	 * 其长度会减半
	 * <p>
	 * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
	 * returned array will be half the length of the passed array, as it takes two characters to represent any given
	 * byte. An exception is thrown if the passed char array has an odd number of elements.
	 *
	 * @param data An array of characters containing hexadecimal digits
	 * @return A byte array containing binary data decoded from the supplied char array.
	 * @throws DecoderException Thrown if an odd number of characters or illegal characters are supplied，异常处理  DecoderException 如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
	 */
	public static byte[] decodeHex(final char[] data) throws DecoderException {

		final byte[] out = new byte[data.length >> 1];
		decodeHex(data, out, 0);
		return out;
	}



	/**
	 * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
	 * returned array will be half the length of the passed array, as it takes two characters to represent any given
	 * byte. An exception is thrown if the passed char array has an odd number of elements.
	 *
	 * @param data      An array of characters containing hexadecimal digits
	 * @param out       A byte array to contain the binary data decoded from the supplied char array.
	 * @param outOffset The position within {@code out} to start writing the decoded bytes.
	 * @return the number of bytes written to {@code out}.
	 * @throws DecoderException Thrown if an odd number of characters or illegal characters are supplied
	 */
	public static int decodeHex(final char[] data, final byte[] out, final int outOffset) throws DecoderException {
		final int len = data.length;

		if ((len & 0x01) != 0) {
			throw new DecoderException("Odd number of characters.");
		}

		final int outLen = len >> 1;
		if (out.length - outOffset < outLen) {
			throw new DecoderException("Output array is not large enough to accommodate decoded data.");
		}

		// two characters form the hex value.
		for (int i = outOffset, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}

		return outLen;
	}

	/**
	 * Converts a String representing hexadecimal values into an array of bytes of those same values. The returned array
	 * will be half the length of the passed String, as it takes two characters to represent any given byte. An
	 * exception is thrown if the passed String has an odd number of elements.
	 *
	 * 将十六进制字符串解码为byte[]
	 * @param data A String containing hexadecimal digits 十六进制String
	 * @return A byte array containing binary data decoded from the supplied char array.
	 * @throws DecoderException Thrown if an odd number of characters or illegal characters are supplied ,异常处理
	 */
	public static byte[] decodeHex(final String data) throws DecoderException {

		return decodeHex(data.toCharArray());
	}


	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 * The returned array will be double the length of the passed array, as it takes two characters to represent any
	 * given byte.
	 * 将字节数组转换为十六进制字符数组
	 *
	 * @param data a byte[] to convert to hex characters
	 * @return A char[] containing lower-case hexadecimal characters,十六进制char[]
	 */
	public static char[] encodeHex(final byte[] data) {
		return encodeHex(data, true);
	}


	/**
	 *
	 * 将字节数组转换为十六进制字符串
	 * Converts a byte buffer into an array of characters representing the hexadecimal values of each byte in order. The
	 * returned array will be double the length of the passed array, as it takes two characters to represent any given
	 * byte.
	 *
	 * <p>All bytes identified by {@link ByteBuffer#remaining()} will be used; after this method
	 * the value {@link ByteBuffer#remaining() remaining()} will be zero.</p>
	 *
	 * @param data        a byte buffer to convert to hex characters
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A char[] containing hexadecimal characters in the selected case
	 */
	public static char[] encodeHex(final ByteBuffer data, final boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * 将字节数组转换为十六进制字符数组
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 * The returned array will be double the length of the passed array, as it takes two characters to represent any
	 * given byte.
	 *
	 * @param data        a byte[] to convert to Hex characters
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase {@code true} 传换成小写格式 ， {@code false} 传换成大写格式
	 * @return A char[] containing hexadecimal characters in the selected case  十六进制char[]
	 */
	public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}


	/**
	 * 将字节数组转换为十六进制字符数组
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 * The returned array will be double the length of the passed array, as it takes two characters to represent any
	 * given byte.
	 *
	 * @param data     a byte[] to convert to hex characters
	 * @param toDigits 用于控制输出的char[] the output alphabet (must contain at least 16 chars)
	 * @return 十六进制char[] A char[] containing the appropriate characters from the alphabet For best results, this should be either
	 * upper- or lower-case hex.
	 */

	public static char[] encodeHex(final byte[] data, final char[] toDigits) {
		final int dataLength = data.length;
		final char[] out = new char[dataLength << 1];
		encodeHex(data, 0, dataLength, toDigits, out, 0);
		return out;
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 *
	 * @param data        a byte[] to convert to hex characters
	 * @param dataOffset  the position in {@code data} to start encoding from
	 * @param dataLen     the number of bytes from {@code dataOffset} to encode
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A char[] containing the appropriate characters from the alphabet For best results, this should be either
	 * upper- or lower-case hex.
	 */
	public static char[] encodeHex(final byte[] data, final int dataOffset, final int dataLen,
								   final boolean toLowerCase) {
		final char[] out = new char[dataLen << 1];
		encodeHex(data, dataOffset, dataLen, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER, out, 0);
		return out;
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 *
	 * @param data        a byte[] to convert to hex characters
	 * @param dataOffset  the position in {@code data} to start encoding from
	 * @param dataLen     the number of bytes from {@code dataOffset} to encode
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @param out         a char[] which will hold the resultant appropriate characters from the alphabet.
	 * @param outOffset   the position within {@code out} at which to start writing the encoded characters.
	 * @since 1.15
	 */
	public static void encodeHex(final byte[] data, final int dataOffset, final int dataLen,
								 final boolean toLowerCase, final char[] out, final int outOffset) {
		encodeHex(data, dataOffset, dataLen, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER, out, outOffset);
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 * <p>
	 * 将字节数组转换为十六进制字符数组
	 *
	 * @param data       a byte[] to convert to hex characters
	 * @param dataOffset the position in {@code data} to start encoding from
	 * @param dataLen    the number of bytes from {@code dataOffset} to encode
	 * @param toDigits   the output alphabet (must contain at least 16 chars)
	 * @param out        a char[] which will hold the resultant appropriate characters from the alphabet.
	 * @param outOffset  the position within {@code out} at which to start writing the encoded characters.
	 */
	private static void encodeHex(final byte[] data, final int dataOffset, final int dataLen, final char[] toDigits,
								  final char[] out, final int outOffset) {
		// two characters form the hex value.
		for (int i = dataOffset, j = outOffset; i < dataOffset + dataLen; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
	}

	/**
	 * Converts a byte buffer into an array of characters representing the hexadecimal values of each byte in order. The
	 * returned array will be double the length of the passed array, as it takes two characters to represent any given
	 * byte.
	 *
	 * <p>All bytes identified by {@link ByteBuffer#remaining()} will be used; after this method
	 * the value {@link ByteBuffer#remaining() remaining()} will be zero.</p>
	 *
	 * @param data a byte buffer to convert to hex characters
	 * @return A char[] containing lower-case hexadecimal characters
	 * @since 1.11
	 */
	public static char[] encodeHex(final ByteBuffer data) {
		return encodeHex(data, true);
	}



	/**
	 * Converts a byte buffer into an array of characters representing the hexadecimal values of each byte in order. The
	 * returned array will be double the length of the passed array, as it takes two characters to represent any given
	 * byte.
	 *
	 * <p>All bytes identified by {@link ByteBuffer#remaining()} will be used; after this method
	 * the value {@link ByteBuffer#remaining() remaining()} will be zero.</p>
	 *
	 * @param byteBuffer a byte buffer to convert to hex characters
	 * @param toDigits   the output alphabet (must be at least 16 characters)
	 * @return A char[] containing the appropriate characters from the alphabet For best results, this should be either
	 * upper- or lower-case hex.
	 * @since 1.11
	 */
	protected static char[] encodeHex(final ByteBuffer byteBuffer, final char[] toDigits) {
		return encodeHex(toByteArray(byteBuffer), toDigits);
	}

	/**
	 * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
	 * String will be double the length of the passed array, as it takes two characters to represent any given byte.
	 *
	 *
	 * 将字节数组转换为十六进制字符串
	 *
	 * @param data byte[] a byte[] to convert to hex characters
	 * @return 十六进制String  A String containing lower-case hexadecimal characters
	 */
	public static String encodeHexStr(byte[] data) {
		return encodeHexStr(data, true);
	}


	/**
	 * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
	 * String will be double the length of the passed array, as it takes two characters to represent any given byte.
	 *
	 * @param data        a byte[] to convert to hex characters
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A String containing lower-case hexadecimal characters
	 * @since 1.11
	 */
	public static String encodeHexStr(final byte[] data, final boolean toLowerCase) {
		return new String(encodeHex(data, toLowerCase));
	}



	/**
	 * Converts a byte buffer into a String representing the hexadecimal values of each byte in order. The returned
	 * String will be double the length of the passed array, as it takes two characters to represent any given byte.
	 *
	 * <p>All bytes identified by {@link ByteBuffer#remaining()} will be used; after this method
	 * the value {@link ByteBuffer#remaining() remaining()} will be zero.</p>
	 *
	 * @param data a byte buffer to convert to hex characters
	 * @return A String containing lower-case hexadecimal characters
	 * @since 1.11
	 */
	public static String encodeHexStr(final ByteBuffer data) {
		return new String(encodeHex(data));
	}

	/**
	 * Converts a byte buffer into a String representing the hexadecimal values of each byte in order. The returned
	 * String will be double the length of the passed array, as it takes two characters to represent any given byte.
	 *
	 * <p>All bytes identified by {@link ByteBuffer#remaining()} will be used; after this method
	 * the value {@link ByteBuffer#remaining() remaining()} will be zero.</p>
	 *
	 * @param data        a byte buffer to convert to hex characters
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A String containing lower-case hexadecimal characters
	 * @since 1.11
	 */
	public static String encodeHexStr(final ByteBuffer data, final boolean toLowerCase) {
		return new String(encodeHex(data, toLowerCase));
	}

	/**
	 * Convert the byte buffer to a byte array. All bytes identified by
	 * {@link ByteBuffer#remaining()} will be used.
	 *
	 * @param byteBuffer the byte buffer
	 * @return the byte[]
	 */
	private static byte[] toByteArray(final ByteBuffer byteBuffer) {
		final int remaining = byteBuffer.remaining();
		// Use the underlying buffer if possible
		if (byteBuffer.hasArray()) {
			final byte[] byteArray = byteBuffer.array();
			if (remaining == byteArray.length) {
				byteBuffer.position(remaining);
				return byteArray;
			}
		}
		// Copy the bytes
		final byte[] byteArray = new byte[remaining];
		byteBuffer.get(byteArray);
		return byteArray;
	}

	/**
	 * 将十六进制字符转换成一个整数
	 * Converts a hexadecimal character to an integer.
	 *
	 * @param ch    A character to convert to an integer digit
	 * @param index The index of the character in the source
	 * @return An integer
	 * @throws DecoderException Thrown if ch is an illegal hex character
	 */
	public static int toDigit(final char ch, final int index) throws DecoderException {
		final int digit = Character.digit(ch, 16);
		//if (digit == -1)
		if (digit < 0) {
			throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
		}
		return digit;
	}


	/**
	 * Converts an array of character bytes representing hexadecimal values into an array of bytes of those same values.
	 * The returned array will be half the length of the passed array, as it takes two characters to represent any given
	 * byte. An exception is thrown if the passed char array has an odd number of elements.
	 *
	 * @param array An array of character bytes containing hexadecimal digits
	 * @param charset  cherset
	 * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
	 * @throws DecoderException Thrown if an odd number of characters is supplied to this function
	 * @see #decodeHex(char[])
	 */
	public static byte[] decode(final byte[] array, final Charset charset) throws DecoderException {
		return decodeHex(new String(array, charset).toCharArray());
	}

	/**
	 * Converts a buffer of character bytes representing hexadecimal values into an array of bytes of those same values.
	 * The returned array will be half the length of the passed array, as it takes two characters to represent any given
	 * byte. An exception is thrown if the passed char array has an odd number of elements.
	 *
	 * <p>All bytes identified by {@link ByteBuffer#remaining()} will be used; after this method
	 * the value {@link ByteBuffer#remaining() remaining()} will be zero.</p>
	 * @param charset  cherset
	 * @param buffer An array of character bytes containing hexadecimal digits
	 * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
	 * @throws DecoderException Thrown if an odd number of characters is supplied to this function
	 * @see #decodeHex(char[])
	 * @since 1.11
	 */
	public static byte[] decode(final ByteBuffer buffer, final Charset charset) throws DecoderException {
		return decodeHex(new String(toByteArray(buffer), charset).toCharArray());
	}

	/**
	 * Converts a String or an array of character bytes representing hexadecimal values into an array of bytes of those
	 * same values. The returned array will be half the length of the passed String or array, as it takes two characters
	 * to represent any given byte. An exception is thrown if the passed char array has an odd number of elements.
	 * @param charset  cherset
	 * @param object A String, ByteBuffer, byte[], or an array of character bytes containing hexadecimal digits
	 * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
	 * @throws DecoderException Thrown if an odd number of characters is supplied to this function or the object is not
	 *                          a String or char[]
	 * @see #decodeHex(char[])
	 */

	public static Object decode(final Object object, Charset charset) throws DecoderException {
		if (object instanceof String) {
			return decode(((String) object).toCharArray(), charset);
		}
		if (object instanceof byte[]) {
			return decode((byte[]) object, charset);
		}
		if (object instanceof ByteBuffer) {
			return decode((ByteBuffer) object, charset);
		}
		try {

			return decodeHex((char[]) object);
		} catch (final ClassCastException e) {
			throw new DecoderException(e.getMessage(), e);
		}
	}

	/**
	 * Converts an array of bytes into an array of bytes for the characters representing the hexadecimal values of each
	 * byte in order. The returned array will be double the length of the passed array, as it takes two characters to
	 * represent any given byte.
	 * <p>
	 * The conversion from hexadecimal characters to the returned bytes is performed with the charset named by
	 * </p>
	 * @param charset  cherset
	 * @param array a byte[] to convert to hex characters
	 * @return A byte[] containing the bytes of the lower-case hexadecimal characters
	 * @see #encodeHex(byte[])
	 * @since 1.7 No longer throws IllegalStateException if the charsetName is invalid.
	 */

	public static byte[] encode(final byte[] array, final Charset charset) {
		return encodeHexStr(array).getBytes(charset);
	}

	/**
	 * Converts byte buffer into an array of bytes for the characters representing the hexadecimal values of each byte
	 * in order. The returned array will be double the length of the passed array, as it takes two characters to
	 * represent any given byte.
	 *
	 * <p>The conversion from hexadecimal characters to the returned bytes is performed with the charset named by
	 *
	 *
	 * <p>All bytes identified by {@link ByteBuffer#remaining()} will be used; after this method
	 * the value {@link ByteBuffer#remaining() remaining()} will be zero.</p>
	 * @param charset  cherset
	 * @param array a byte buffer to convert to hex characters
	 * @return A byte[] containing the bytes of the lower-case hexadecimal characters
	 * @see #encodeHex(byte[])
	 */
	public static byte[] encode(final ByteBuffer array, final Charset charset) {
		return encodeHexStr(array).getBytes(charset);
	}

	/**
	 * Converts a String or an array of bytes into an array of characters representing the hexadecimal values of each
	 * byte in order. The returned array will be double the length of the passed String or array, as it takes two
	 * characters to represent any given byte.
	 * <p>
	 * The conversion from hexadecimal characters to bytes to be encoded to performed with the charset named by
	 * </p>
	 * @param charset  cherset
	 * @param object a String, ByteBuffer, or byte[] to convert to hex characters
	 * @return A char[] containing lower-case hexadecimal characters
	 * @throws EncoderException Thrown if the given object is not a String or byte[]
	 * @see #encodeHex(byte[])
	 */

	public static Object encode(final Object object, final Charset charset) throws EncoderException {
		final byte[] byteArray;
		if (object instanceof String) {
			byteArray = ((String) object).getBytes(charset);
		} else if (object instanceof ByteBuffer) {
			byteArray = toByteArray((ByteBuffer) object);
		} else {
			try {
				byteArray = (byte[]) object;
			} catch (final ClassCastException e) {
				throw new EncoderException(e.getMessage(), e);
			}
		}
		return encodeHex(byteArray);
	}



	/**
	 * 判断给定字符串是否为16进制数<br>
	 * 如果是，需要使用对应数字类型对象的{@code decode}方法解码<br>
	 * 例如：{@code Integer.decode}方法解码int类型的16进制数字
	 *
	 * @param value 值
	 * @return 是否为16进制
	 */
	public static boolean isHexNumber(String value) {
		final int index = (value.startsWith("-") ? 1 : 0);
		if (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index)) {
			try {
				//noinspection ResultOfMethodCallIgnored
				Long.decode(value);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}

		return false;
	}

	// ---------------------------------------------------------------------------------------------------- encode



	/**
	 * 将字节数组转换为十六进制字符数组
	 *
	 * @param str     字符串
	 * @param charset 编码
	 * @return 十六进制char[]
	 */
	public static char[] encodeHex(String str, Charset charset) {
		return encodeHex(StrUtil.bytes(str, charset), true);
	}





	/**
	 * 将字符串转换为十六进制字符串，结果为小写
	 *
	 * @param data    需要被编码的字符串
	 * @param charset 编码
	 * @return 十六进制String
	 */
	public static String encodeHexStr(String data, Charset charset) {
		return encodeHexStr(StrUtil.bytes(data, charset), true);
	}

	/**
	 * 将字符串转换为十六进制字符串，结果为小写，默认编码是UTF-8
	 *
	 * @param data 被编码的字符串
	 * @return 十六进制String
	 */
	public static String encodeHexStr(String data) {
		return encodeHexStr(data, CharsetUtil.CHARSET_UTF_8);
	}


	// ---------------------------------------------------------------------------------------------------- decode

	/**
	 * 将十六进制字符数组转换为字符串，默认编码UTF-8
	 *
	 * @param hexStr 十六进制String
	 * @return 字符串
	 * @throws DecoderException  异常处理
	 */
	public static String decodeHexStr(String hexStr) throws DecoderException {
		return decodeHexStr(hexStr, CharsetUtil.CHARSET_UTF_8);
	}

	/**
	 * 将十六进制字符数组转换为字符串
	 *
	 * @param hexStr  十六进制String
	 * @param charset 编码
	 * @return 字符串
	 * @throws DecoderException 异常处理
	 */
	public static String decodeHexStr(String hexStr, Charset charset) throws DecoderException {
		if (StrUtil.isEmpty(hexStr)) {
			return hexStr;
		}
		return StrUtil.str(decodeHex(hexStr), charset);
	}

	/**
	 * 将十六进制字符数组转换为字符串
	 *
	 * @param hexData 十六进制char[]
	 * @param charset 编码
	 * @return 字符串
	 * @throws DecoderException 异常处理
	 */
	public static String decodeHexStr(char[] hexData, Charset charset) throws DecoderException {
		return StrUtil.str(decodeHex(hexData), charset);
	}







	// ---------------------------------------------------------------------------------------- Color

	/**
	 * 将{@link Color}编码为Hex形式
	 *
	 * @param color {@link Color}
	 * @return Hex字符串
	 * @since 1.0.0
	 */
	public static String encodeColor(Color color) {
		return encodeColor(color, "#");
	}

	/**
	 * 将{@link Color}编码为Hex形式
	 *
	 * @param color  {@link Color}
	 * @param prefix 前缀字符串，可以是#、0x等
	 * @return Hex字符串
	 * @since 1.0.0
	 */
	public static String encodeColor(Color color, String prefix) {
		final StringBuilder builder = new StringBuilder(prefix);
		String colorHex;
		colorHex = Integer.toHexString(color.getRed());
		if (1 == colorHex.length()) {
			builder.append('0');
		}
		builder.append(colorHex);
		colorHex = Integer.toHexString(color.getGreen());
		if (1 == colorHex.length()) {
			builder.append('0');
		}
		builder.append(colorHex);
		colorHex = Integer.toHexString(color.getBlue());
		if (1 == colorHex.length()) {
			builder.append('0');
		}
		builder.append(colorHex);
		return builder.toString();
	}

	/**
	 * 将Hex颜色值转为
	 *
	 * @param hexColor 16进制颜色值，可以以#开头，也可以用0x开头
	 * @return {@link Color}
	 * @since 1.0.0
	 */
	public static Color decodeColor(String hexColor) {
		return Color.decode(hexColor);
	}

	/**
	 * 将指定int值转换为Unicode字符串形式，常用于特殊字符（例如汉字）转Unicode形式<br>
	 * 转换的字符串如果u后不足4位，则前面用0填充，例如：
	 *
	 * <pre>
	 * '你' =》\u4f60
	 * </pre>
	 *
	 * @param value int值，也可以是char
	 * @return Unicode表现形式
	 */
	public static String toUnicodeHex(int value) {
		final StringBuilder builder = new StringBuilder(6);

		builder.append("\\u");
		String hex = toHex(value);
		int len = hex.length();
		if (len < 4) {
			builder.append("0000", 0, 4 - len);// 不足4位补0
		}
		builder.append(hex);

		return builder.toString();
	}

	/**
	 * 将指定char值转换为Unicode字符串形式，常用于特殊字符（例如汉字）转Unicode形式<br>
	 * 转换的字符串如果u后不足4位，则前面用0填充，例如：
	 *
	 * <pre>
	 * '你' =》'\u4f60'
	 * </pre>
	 *
	 * @param ch char值
	 * @return Unicode表现形式
	 * @since 1.0.0
	 */
	public static String toUnicodeHex(char ch) {
		return "\\u" +//
				DIGITS_LOWER[(ch >> 12) & 15] +//
				DIGITS_LOWER[(ch >> 8) & 15] +//
				DIGITS_LOWER[(ch >> 4) & 15] +//
				DIGITS_LOWER[(ch) & 15];
	}

	/**
	 * 转为16进制字符串
	 *
	 * @param value int值
	 * @return 16进制字符串
	 * @since 1.0.0
	 */
	public static String toHex(int value) {
		return Integer.toHexString(value);
	}

	/**
	 * 16进制字符串转为int
	 *
	 * @param value 16进制字符串
	 * @return 16进制字符串int值
	 * @since 1.0.0
	 */
	public static int hexToInt(String value) {
		return Integer.parseInt(value, 16);
	}

	/**
	 * 转为16进制字符串
	 *
	 * @param value int值
	 * @return 16进制字符串
	 * @since 1.0.0
	 */
	public static String toHex(long value) {
		return Long.toHexString(value);
	}

	/**
	 * 16进制字符串转为long
	 *
	 * @param value 16进制字符串
	 * @return long值
	 * @since 1.0.0
	 */
	public static long hexToLong(String value) {
		return Long.parseLong(value, 16);
	}

	/**
	 * 将byte值转为16进制并添加到{@link StringBuilder}中
	 *
	 * @param builder     {@link StringBuilder}
	 * @param b           byte
	 * @param toLowerCase 是否使用小写
	 * @since 1.0.0
	 */
	public static void appendHex(StringBuilder builder, byte b, boolean toLowerCase) {
		final char[] toDigits = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;

		int high = (b & 0xf0) >>> 4;//高位
		int low = b & 0x0f;//低位
		builder.append(toDigits[high]);
		builder.append(toDigits[low]);
	}

	/**
	 * Hex（16进制）字符串转为BigInteger
	 *
	 * @param hexStr Hex(16进制字符串)
	 * @return {@link BigInteger}
	 * @since 1.0.0
	 */
	public static BigInteger toBigInteger(String hexStr) {
		if (null == hexStr) {
			return null;
		}
		return new BigInteger(hexStr, 16);
	}

	/**
	 * 格式化Hex字符串，结果为每2位加一个空格，类似于：
	 * <pre>
	 *     e8 8c 67 03 80 cb 22 00 95 26 8f
	 * </pre>
	 *
	 * @param hexStr Hex字符串
	 * @return 格式化后的字符串
	 */
	public static String format(String hexStr) {
		final int length = hexStr.length();
		final StringBuilder builder = StrUtil.builder(length + length / 2);
		builder.append(hexStr.charAt(0)).append(hexStr.charAt(1));
		for (int i = 2; i < length - 1; i += 2) {
			builder.append(CharUtil.SPACE).append(hexStr.charAt(i)).append(hexStr.charAt(i + 1));
		}
		return builder.toString();
	}

}
