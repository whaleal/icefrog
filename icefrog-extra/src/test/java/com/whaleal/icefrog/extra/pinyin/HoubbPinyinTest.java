package com.whaleal.icefrog.extra.pinyin;

import com.whaleal.icefrog.extra.pinyin.engine.houbbpinyin.HoubbPinyinEngine;
import org.junit.Assert;
import org.junit.Test;

public class HoubbPinyinTest {

    final HoubbPinyinEngine engine = new HoubbPinyinEngine();

    @Test
    public void getFirstLetterTest() {
        final String result = engine.getFirstLetter("林海", "");
        Assert.assertEquals("lh", result);
    }

    @Test
    public void getPinyinTest() {
        final String pinyin = engine.getPinyin("你好h", " ");
        Assert.assertEquals("ni hao h", pinyin);
    }
}
