package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.codec.DecoderException;
import com.whaleal.icefrog.core.codec.Hex;

import java.awt.Color;
import java.math.BigInteger;
import java.nio.charset.Charset;

/**
 * 十六进制（简写为hex或下标16）在数学中是一种逢16进1的进位制，一般用数字0到9和字母A到F表示（其中:A~F即10~15）。<br>
 * 例如十进制数57，在二进制写作111001，在16进制写作39。<br>
 * 像java,c这样的语言为了区分十六进制和十进制数值,会在十六进制数的前面加上 0x,比如0x20是十进制的32,而不是十进制的20<br>
 * <p>
 * 参考：https://my.oschina.net/xinxingegeya/blog/287476
 *
 * @see com.whaleal.icefrog.core.codec.Hex
 * 内部调用使用Hex的相关操作
 *
 * @author Looly
 * @author wh
 */
public class HexUtil  extends Hex{


}
