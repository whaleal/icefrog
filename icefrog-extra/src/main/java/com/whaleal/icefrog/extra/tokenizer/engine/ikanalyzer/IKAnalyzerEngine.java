package com.whaleal.icefrog.extra.tokenizer.engine.ikanalyzer;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.extra.tokenizer.Result;
import com.whaleal.icefrog.extra.tokenizer.TokenizerEngine;
import org.wltea.analyzer.core.IKSegmenter;

/**
 * IKAnalyzer分词引擎实现<br>
 * 项目地址：https://github.com/yozhao/IKAnalyzer
 *
 * @author Looly
 * @author wh
 */
public class IKAnalyzerEngine implements TokenizerEngine {

    private final IKSegmenter seg;

    /**
     * 构造
     */
    public IKAnalyzerEngine() {
        this(new IKSegmenter(null, true));
    }

    /**
     * 构造
     *
     * @param seg {@link IKSegmenter}
     */
    public IKAnalyzerEngine( IKSegmenter seg ) {
        this.seg = seg;
    }

    @Override
    public Result parse( CharSequence text ) {
        this.seg.reset(StrUtil.getReader(text));
        return new IKAnalyzerResult(this.seg);
    }
}
