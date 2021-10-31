package com.whaleal.icefrog.extra.tokenizer.engine.ikanalyzer;

import com.whaleal.icefrog.extra.tokenizer.Word;
import org.wltea.analyzer.core.Lexeme;

/**
 * IKAnalyzer分词中的一个单词包装
 *
 * @author Looly
 * @author wh
 */
public class IKAnalyzerWord implements Word {
    private static final long serialVersionUID = 1L;

    private final Lexeme word;

    /**
     * 构造
     *
     * @param word {@link Lexeme}
     */
    public IKAnalyzerWord( Lexeme word ) {
        this.word = word;
    }

    @Override
    public String getText() {
        return word.getLexemeText();
    }

    @Override
    public int getStartOffset() {
        return word.getBeginPosition();
    }

    @Override
    public int getEndOffset() {
        return word.getEndPosition();
    }

    @Override
    public String toString() {
        return getText();
    }
}
