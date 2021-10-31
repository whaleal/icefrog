package com.whaleal.icefrog.extra.tokenizer.engine.mmseg;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.extra.tokenizer.Result;
import com.whaleal.icefrog.extra.tokenizer.TokenizerEngine;

import java.io.StringReader;

/**
 * mmseg4j分词引擎实现<br>
 * 项目地址：https://github.com/chenlb/mmseg4j-core
 *
 * @author Looly
 * @author wh
 */
public class MmsegEngine implements TokenizerEngine {

    private final MMSeg mmSeg;

    /**
     * 构造
     */
    public MmsegEngine() {
        final Dictionary dict = Dictionary.getInstance();
        final ComplexSeg seg = new ComplexSeg(dict);
        this.mmSeg = new MMSeg(new StringReader(""), seg);
    }

    /**
     * 构造
     *
     * @param mmSeg 模式{@link MMSeg}
     */
    public MmsegEngine( MMSeg mmSeg ) {
        this.mmSeg = mmSeg;
    }

    @Override
    public Result parse( CharSequence text ) {
        this.mmSeg.reset(StrUtil.getReader(text));
        return new MmsegResult(this.mmSeg);
    }

}
