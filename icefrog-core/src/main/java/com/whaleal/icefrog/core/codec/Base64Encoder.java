package com.whaleal.icefrog.core.codec;

import com.whaleal.icefrog.core.util.CharsetUtil;
import com.whaleal.icefrog.core.util.StrUtil;

import java.nio.charset.Charset;

/**
 * Base64编码
 *
 * Base64就是一种基于64个可打印字符来表示二进制数据的方法。可查看RFC2045～RFC2049
 *
 * 关于这个编码的规则：
 * ①.把3个字节变成4个字节。
 * ②每76个字符加一个换行符。
 * ③.最后的结束符也要处理。
 *
 * 关于以上第一点 就是三个byte  一组 返回 四个字节 不足时补全
 * 转换前 10101101,10111010,01110110
 * 转换后 00101011, 00011011 ,00101001 ,00110110
 * 将转换前的数据6个一切分头部加0，生成一个新的 byte
 * 直接位运算 分别需要移动 18位 12位 6位 0位
 *
 *
 * 转换原理如下
 * ① 将字符串 转为字符  即char的组成序列 而每一个字符char  【 a 】  即为一个Byte 占用8个bit 【a 】 ~  01100001
 * ② 将每一个byte  都转为 bit ，同时保留 首部的0  转到得到一个8*n 的字符串
 * ③ base64  是将8位码 转为6位码  这个时候就会出现不足6位的情况 后面直接补0 01100001 ~  011000 010000  补了4个0
 * ④ 查看base64 编码表   24  011000	Y  16	010000	Q  得到大写字母 Y Q
 * ⑤ 根据补0 的个数添加等号 两个0 换一个 = ，因为补0 个数为4 所以 补两个 ==
 * ⑥ 编码值为 YQ==
 *
 * 编码是三个byte 为一组 ，共同作用返回四个字符 。当传入或者剩余不足3个长度时 ，也返回四个字符 必要时 用 == 补全
 *
 *
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class Base64Encoder {

	private static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
	/** 标准编码表
	 * https://en.wikipedia.org/wiki/Base64
	 *
	 * */
	private static final byte[] STANDARD_ENCODE_TABLE = { //
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', //
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', //
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', //
			'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', //
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', //
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', //
			'w', 'x', 'y', 'z', '0', '1', '2', '3', //
			'4', '5', '6', '7', '8', '9', '+', '/' //
	};
	/** URL安全的编码表，将 + 和 / 替换为 - 和 _ */
	private static final byte[] URL_SAFE_ENCODE_TABLE = { //
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', //
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', //
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', //
			'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', //
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', //
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', //
			'w', 'x', 'y', 'z', '0', '1', '2', '3', //
			'4', '5', '6', '7', '8', '9', '-', '_' //
	};

	// -------------------------------------------------------------------- encode
	/**
	 * 编码为Base64，非URL安全的
	 *
	 * @param arr 被编码的数组
	 * @param lineSep 在76个char之后是CRLF还是EOF
	 * @return 编码后的bytes
	 */
	public static byte[] encode(byte[] arr, boolean lineSep) {
		return encode(arr, lineSep, false);
	}

	/**
	 * 编码为Base64，URL安全的
	 *
	 * @param arr 被编码的数组
	 * @param lineSep 在76个char之后是CRLF还是EOF
	 * @return 编码后的bytes
	 * @since 1.0.0
	 */
	public static byte[] encodeUrlSafe(byte[] arr, boolean lineSep) {
		return encode(arr, lineSep, true);
	}

	/**
	 * base64编码
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String encode(CharSequence source) {
		return encode(source, DEFAULT_CHARSET);
	}

	/**
	 * base64编码，URL安全
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 * @since 1.0.0
	 */
	public static String encodeUrlSafe(CharSequence source) {
		return encodeUrlSafe(source, DEFAULT_CHARSET);
	}

	/**
	 * base64编码
	 *
	 * @param source 被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String encode(CharSequence source, Charset charset) {
		return encode(StrUtil.bytes(source, charset));
	}

	/**
	 * base64编码，URL安全的
	 *
	 * @param source 被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 * @since 1.0.0
	 */
	public static String encodeUrlSafe(CharSequence source, Charset charset) {
		return encodeUrlSafe(StrUtil.bytes(source, charset));
	}

	/**
	 * base64编码
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String encode(byte[] source) {
		return StrUtil.str(encode(source, false), DEFAULT_CHARSET);
	}

	/**
	 * base64编码,URL安全的
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 * @since 1.0.0
	 */
	public static String encodeUrlSafe(byte[] source) {
		return StrUtil.str(encodeUrlSafe(source, false), DEFAULT_CHARSET);
	}

	/**
	 * 编码为Base64字符串<br>
	 * 如果isMultiLine为{@code true}，则每76个字符一个换行符，否则在一行显示
	 *
	 * @param arr 被编码的数组
	 * @param isMultiLine 在76个char之后是CRLF还是EOF
	 * @param isUrlSafe 是否使用URL安全字符，在URL Safe模式下，=为URL中的关键字符，不需要补充。空余的byte位要去掉，一般为{@code false}
	 * @return 编码后的bytes
	 * @since 1.0.0
	 */
	public static String encodeStr(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
		return StrUtil.str(encode(arr, isMultiLine, isUrlSafe), DEFAULT_CHARSET);
	}

	/**
	 * <p>
	 * 编码为Base64<br>
	 * 如果isMultiLine为{@code true}，则每76个字符一个换行符，否则在一行显示 . <br>
	 * <p>
	 *
	 * @param arr         被编码的数组
	 * @param isMultiLine 在76个char之后是CRLF还是EOF
	 * @param isUrlSafe   是否使用URL安全字符，在URL Safe模式下，=为URL中的关键字符，不需要补充。空余的byte位要去掉，一般为{@code false}
	 * @return 编码后的bytes
	 */
	public static byte[] encode(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {

		if (null == arr) {
			return null;
		}

		/**
		 * 获取传入的byte Arr 的长度
		 */
		int len = arr.length;
		if (len == 0) {
			return new byte[0];
		}



		int evenlen = (len / 3) * 3;
		// 返回的 encode 字符串的长度
		int cnt = ((len - 1) / 3 + 1) << 2;
		int destlen = cnt + (isMultiLine ? (cnt - 1) / 76 << 1 : 0);
		byte[] dest = new byte[destlen];

		// 选择一个 encode 表
		byte[] encodeTable = isUrlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;

		// 这一部分与 apache  common-io 原理相同
		// *字符串"0xFF"表示的是16进制(十进制是255)表示为二进制的值为{@code '11111111'};那么'&'符表示的是按位数进行与（同为1的时候返回1,否则返回0）;<br>
		//	 字	符串"0x3f"代表16进制数3F;即 {@code '00111111B'} 前2位自动充为0,后6位全部为1; '&' 运算后保留后六位，前两位置为0 ;<br>
		for (int s = 0, d = 0, cc = 0; s < evenlen;) {
			int i = (arr[s++] & 0xff) << 16 | (arr[s++] & 0xff) << 8 | (arr[s++] & 0xff);

			dest[d++] = encodeTable[(i >>> 18) & 0x3f];
			dest[d++] = encodeTable[(i >>> 12) & 0x3f];
			dest[d++] = encodeTable[(i >>> 6) & 0x3f];
			dest[d++] = encodeTable[i & 0x3f];

			if (isMultiLine && ++cc == 19 && d < destlen - 2) {
				dest[d++] = '\r';
				dest[d++] = '\n';
				cc = 0;
			}
		}

		int left = len - evenlen;// 剩余位数
		if (left > 0) {
			int i = ((arr[evenlen] & 0xff) << 10) | (left == 2 ? ((arr[len - 1] & 0xff) << 2) : 0);

			dest[destlen - 4] = encodeTable[i >> 12];
			dest[destlen - 3] = encodeTable[(i >>> 6) & 0x3f];

			if (isUrlSafe) {
				// 在URL Safe模式下，=为URL中的关键字符，不需要补充。空余的byte位要去掉。
				int urlSafeLen = destlen - 2;
				if (2 == left) {
					dest[destlen - 2] = encodeTable[i & 0x3f];
					urlSafeLen += 1;
				}
				byte[] urlSafeDest = new byte[urlSafeLen];
				System.arraycopy(dest, 0, urlSafeDest, 0, urlSafeLen);
				return urlSafeDest;
			} else {
				dest[destlen - 2] = (left == 2) ? encodeTable[i & 0x3f] : (byte) '=';
				dest[destlen - 1] = '=';
			}
		}
		return dest;
	}
}
