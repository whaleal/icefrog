package com.whaleal.icefrog.core.text.escape;

import com.whaleal.icefrog.core.text.replacer.LookupReplacer;

/**
 * HTML4的UNESCAPE
 *
 * @author Looly
 * @author wh
 */
public class Html4Unescape extends XmlUnescape {
    protected static final String[][] ISO8859_1_UNESCAPE = InternalEscapeUtil.invert(Html4Escape.ISO8859_1_ESCAPE);
    protected static final String[][] HTML40_EXTENDED_UNESCAPE = InternalEscapeUtil.invert(Html4Escape.HTML40_EXTENDED_ESCAPE);
    private static final long serialVersionUID = 1L;

    public Html4Unescape() {
        super();
        addChain(new LookupReplacer(ISO8859_1_UNESCAPE));
        addChain(new LookupReplacer(HTML40_EXTENDED_UNESCAPE));
    }
}
