package com.whaleal.icefrog.extra.tokenizer.engine.jieba;

import com.huaban.analysis.jieba.SegToken;
import com.whaleal.icefrog.extra.tokenizer.Result;
import com.whaleal.icefrog.extra.tokenizer.Word;

import java.util.Iterator;
import java.util.List;

/**
 * Jieba分词结果实现<br>
 * 项目地址：https://github.com/huaban/jieba-analysis
 *
 * @author Looly
 * @author wh
 */
public class JiebaResult implements Result {

    Iterator<SegToken> result;

    /**
     * 构造
     *
     * @param segTokenList 分词结果
     */
    public JiebaResult( List<SegToken> segTokenList ) {
        this.result = segTokenList.iterator();
    }

    @Override
    public boolean hasNext() {
        return result.hasNext();
    }

    @Override
    public Word next() {
        return new JiebaWord(result.next());
    }

    @Override
    public void remove() {
        result.remove();
    }

    @Override
    public Iterator<Word> iterator() {
        return this;
    }

}
