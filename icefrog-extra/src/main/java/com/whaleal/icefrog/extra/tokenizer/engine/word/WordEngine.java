package com.whaleal.icefrog.extra.tokenizer.engine.word;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.extra.tokenizer.Result;
import com.whaleal.icefrog.extra.tokenizer.TokenizerEngine;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;

/**
 * Word分词引擎实现<br>
 * 项目地址：https://github.com/ysc/word
 *
 * @author Looly
 * @author wh
 */
public class WordEngine implements TokenizerEngine {

    private final Segmentation segmentation;

    /**
     * 构造
     */
    public WordEngine() {
        this(SegmentationAlgorithm.BidirectionalMaximumMatching);
    }

    /**
     * 构造
     *
     * @param algorithm {@link SegmentationAlgorithm}分词算法枚举
     */
    public WordEngine( SegmentationAlgorithm algorithm ) {
        this(SegmentationFactory.getSegmentation(algorithm));
    }

    /**
     * 构造
     *
     * @param segmentation {@link Segmentation}分词实现
     */
    public WordEngine( Segmentation segmentation ) {
        this.segmentation = segmentation;
    }

    @Override
    public Result parse( CharSequence text ) {
        return new WordResult(this.segmentation.seg(StrUtil.str(text)));
    }

}
