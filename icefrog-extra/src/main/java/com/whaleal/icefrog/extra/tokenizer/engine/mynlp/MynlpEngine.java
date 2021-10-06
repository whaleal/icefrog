package com.whaleal.icefrog.extra.tokenizer.engine.mynlp;

import com.mayabot.nlp.segment.Lexer;
import com.mayabot.nlp.segment.Lexers;
import com.mayabot.nlp.segment.Sentence;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.extra.tokenizer.Result;
import com.whaleal.icefrog.extra.tokenizer.TokenizerEngine;

/**
 * MYNLP 中文NLP工具包分词实现<br>
 * 项目地址：https://github.com/mayabot/mynlp/
 *
 * @author Looly
 * @author wh
 *
 */
public class MynlpEngine implements TokenizerEngine {

	private final Lexer lexer;

	/**
	 * 构造
	 */
	public MynlpEngine() {
		this.lexer = Lexers.core();
	}

	/**
	 * 构造
	 *
	 * @param lexer 分词器接口{@link Lexer}
	 */
	public MynlpEngine(Lexer lexer) {
		this.lexer = lexer;
	}

	@Override
	public Result parse(CharSequence text) {
		final Sentence sentence = this.lexer.scan(StrUtil.str(text));
		return new MynlpResult(sentence);
	}

}
