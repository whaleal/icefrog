package com.whaleal.icefrog.core.text.escape;

import com.whaleal.icefrog.core.text.replacer.LookupReplacer;
import com.whaleal.icefrog.core.text.replacer.ReplacerChain;

/**
 * XML特殊字符转义<br>
 * 见：https://stackoverflow.com/questions/1091945/what-characters-do-i-need-to-escape-in-xml-documents<br>
 *
 * <pre>
 * 	 &amp; (ampersand) 替换为 &amp;amp;
 * 	 &lt; (less than) 替换为 &amp;lt;
 * 	 &gt; (greater than) 替换为 &amp;gt;
 * 	 &quot; (double quote) 替换为 &amp;quot;
 * 	 ' (single quote / apostrophe) 替换为 &amp;apos;
 * </pre>
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class XmlEscape extends ReplacerChain {
    protected static final String[][] BASIC_ESCAPE = { //
//			{"'", "&apos;"}, // " - single-quote
            {"\"", "&quot;"}, // " - double-quote
            {"&", "&amp;"}, // & - ampersand
            {"<", "&lt;"}, // < - less-than
            {">", "&gt;"}, // > - greater-than
    };
    private static final long serialVersionUID = 1L;

    /**
     * 构造
     */
    public XmlEscape() {
        addChain(new LookupReplacer(BASIC_ESCAPE));
    }
}
