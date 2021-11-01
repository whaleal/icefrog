package com.whaleal.icefrog.extra.tokenizer.engine.jcseg;

import com.whaleal.icefrog.extra.tokenizer.AbstractResult;
import com.whaleal.icefrog.extra.tokenizer.TokenizerException;
import com.whaleal.icefrog.extra.tokenizer.Word;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;

import java.io.IOException;

/**
 * Jcseg分词结果包装<br>
 * 项目地址：https://gitee.com/lionsoul/jcseg
 *
 * @author looly
 *
 */
public class JcsegResult extends AbstractResult {

	private final ISegment result;

	/**
	 * 构造
	 * @param segment 分词结果
	 */
	public JcsegResult(ISegment segment) {
		this.result = segment;
	}

	@Override
	protected Word nextWord() {
		IWord word;
		try {
			word = this.result.next();
		} catch (IOException e) {
			throw new TokenizerException(e);
		}
		if(null == word){
			return null;
		}
		return new JcsegWord(word);
	}
}
