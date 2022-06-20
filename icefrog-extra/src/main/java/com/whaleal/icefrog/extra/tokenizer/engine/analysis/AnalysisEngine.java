package com.whaleal.icefrog.extra.tokenizer.engine.analysis;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.extra.tokenizer.Result;
import com.whaleal.icefrog.extra.tokenizer.TokenizerEngine;
import com.whaleal.icefrog.extra.tokenizer.TokenizerException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;

/**
 * Lucene-analysis分词抽象封装<br>
 * 项目地址：https://github.com/apache/lucene-solr/tree/master/lucene/analysis
 *
 * @author looly
 */
public class AnalysisEngine implements TokenizerEngine {

    private final Analyzer analyzer;

    /**
     * 构造
     *
     * @param analyzer 分析器{@link Analyzer}
     */
    public AnalysisEngine( Analyzer analyzer ) {
        this.analyzer = analyzer;
    }

    @Override
    public Result parse( CharSequence text ) {
        TokenStream stream;
        try {
            stream = analyzer.tokenStream("text", StrUtil.str(text));
            stream.reset();
        } catch (IOException e) {
            throw new TokenizerException(e);
        }
        return new AnalysisResult(stream);
    }

}
