package com.whaleal.icefrog.extra.emoji.transformer;


import com.whaleal.icefrog.extra.emoji.fitzpatrick.FitzpatrickAction;
import com.whaleal.icefrog.extra.emoji.model.UnicodeCandidate;

/**
 * emoji transformer
 *
 * @author xuxueli 2018-07-06 20:15:22
 */
public interface EmojiTransformer {

    /**
     * @param unicodeCandidate unicodeCandidate
     * @param fitzpatrickAction     the action to apply for the fitzpatrick modifiers
     * @return return 
     */
    public String transform(UnicodeCandidate unicodeCandidate, FitzpatrickAction fitzpatrickAction);

}