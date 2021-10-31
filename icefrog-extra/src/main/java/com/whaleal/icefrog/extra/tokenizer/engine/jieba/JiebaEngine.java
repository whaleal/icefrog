package com.whaleal.icefrog.extra.tokenizer.engine.jieba;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.extra.tokenizer.Result;
import com.whaleal.icefrog.extra.tokenizer.TokenizerEngine;

/**
 * Jieba分词引擎实现<br>
 * 项目地址：https://github.com/huaban/jieba-analysis
 *
 * @author Looly
 * @author wh
 */
public class JiebaEngine implements TokenizerEngine {

    private final JiebaSegmenter jiebaSegmenter;
    private final SegMode mode;

    /**
     * 构造
     */
    public JiebaEngine() {
        this(SegMode.SEARCH);
    }

    /**
     * 构造
     *
     * @param mode 模式{@link SegMode}
     */
    public JiebaEngine( SegMode mode ) {
        this.jiebaSegmenter = new JiebaSegmenter();
        this.mode = mode;
    }

    @Override
    public Result parse( CharSequence text ) {
        return new JiebaResult(jiebaSegmenter.process(StrUtil.str(text), mode));
    }

}
