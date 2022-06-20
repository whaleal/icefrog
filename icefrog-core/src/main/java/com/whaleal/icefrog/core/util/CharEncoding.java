

package com.whaleal.icefrog.core.util;

/**
 * Character encoding names required of every implementation of the Java platform.
 * <p>
 * From the Java documentation <a
 * href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>:
 * <p>
 * <cite>Every implementation of the Java platform is required to support the following character encodings. Consult the
 * release documentation for your implementation to see if any other encodings are supported. Consult the release
 * documentation for your implementation to see if any other encodings are supported.</cite>
 * </p>
 *
 * <ul>
 * <li>{@code US-ASCII}<p>
 * Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set.</p></li>
 * <li>{@code ISO-8859-1}<p>
 * ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1.</p></li>
 * <li>{@code UTF-8}<p>
 * Eight-bit Unicode Transformation Format.</p></li>
 * <li>{@code UTF-16BE}<p>
 * Sixteen-bit Unicode Transformation Format, big-endian byte order.</p></li>
 * <li>{@code UTF-16LE}<p>
 * Sixteen-bit Unicode Transformation Format, little-endian byte order.</p></li>
 * <li>{@code UTF-16}<p>
 * Sixteen-bit Unicode Transformation Format, byte order specified by a mandatory initial byte-order mark (either order
 * accepted on input, big-endian used on output.)</p></li>
 * </ul>
 * <p>
 * This perhaps would best belong in the [lang] project. Even if a similar interface is defined in [lang], it is not
 * foreseen that [codec] would be made to depend on [lang].
 *
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 * <p>
 * see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html"> Standard charsets </a>
 * </p>
 * <p>
 * CharEncodeing 的静态编写
 *
 * @author wh
 * @see java.nio.charset.StandardCharsets
 * <p>
 * 与StandardCharsets 对应
 * @since 1.1
 */
public class CharEncoding {

    /**
     * CharEncodingISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1.
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     */
    public static final String ISO_8859_1 = "ISO-8859-1";

    /**
     * Seven-bit ASCII, also known as ISO646-US, also known as the Basic Latin block of the Unicode character set.
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     */
    public static final String US_ASCII = "US-ASCII";

    /**
     * Sixteen-bit Unicode Transformation Format, The byte order specified by a mandatory initial byte-order mark
     * (either order accepted on input, big-endian used on output)
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     */
    public static final String UTF_16 = "UTF-16";

    /**
     * Sixteen-bit Unicode Transformation Format, big-endian byte order.
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     */
    public static final String UTF_16BE = "UTF-16BE";

    /**
     * Sixteen-bit Unicode Transformation Format, little-endian byte order.
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     */
    public static final String UTF_16LE = "UTF-16LE";

    /**
     * Eight-bit Unicode Transformation Format.
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     */
    public static final String UTF_8 = "UTF-8";


    /**
     * GBK 编码格式
     */
    public static final String GBK = "GBK";
}
