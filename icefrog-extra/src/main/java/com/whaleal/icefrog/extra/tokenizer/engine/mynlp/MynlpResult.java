package com.whaleal.icefrog.extra.tokenizer.engine.mynlp;

import com.mayabot.nlp.segment.Sentence;
import com.mayabot.nlp.segment.WordTerm;
import com.whaleal.icefrog.extra.tokenizer.Result;
import com.whaleal.icefrog.extra.tokenizer.Word;

import java.util.Iterator;

/**
 * MYNLP 中文NLP工具包分词结果实现<br>
 * 项目地址：https://github.com/mayabot/mynlp/
 *
 * @author Looly
 * @author wh
 */
public class MynlpResult implements Result {

    private final Iterator<WordTerm> result;

    /**
     * 构造
     *
     * @param sentence 分词结果（中文句子）
     */
    public MynlpResult( Sentence sentence ) {
        this.result = sentence.iterator();
    }

    @Override
    public boolean hasNext() {
        return result.hasNext();
    }

    @Override
    public Word next() {
        return new MynlpWord(result.next());
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
