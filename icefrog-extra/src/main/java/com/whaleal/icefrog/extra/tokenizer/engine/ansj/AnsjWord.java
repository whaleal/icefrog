package com.whaleal.icefrog.extra.tokenizer.engine.ansj;

import com.whaleal.icefrog.extra.tokenizer.Word;
import org.ansj.domain.Term;

/**
 * Ansj分词中的一个单词包装
 *
 * @author looly
 */
public class AnsjWord implements Word {
    private static final long serialVersionUID = 1L;

    private final Term term;

    /**
     * 构造
     *
     * @param term {@link Term}
     */
    public AnsjWord( Term term ) {
        this.term = term;
    }

    @Override
    public String getText() {
        return term.getName();
    }

    @Override
    public int getStartOffset() {
        return this.term.getOffe();
    }

    @Override
    public int getEndOffset() {
        return getStartOffset() + getText().length();
    }

    @Override
    public String toString() {
        return getText();
    }
}
