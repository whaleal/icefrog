package com.whaleal.icefrog.core.codec;

import org.junit.Assert;
import org.junit.Test;

public class PunyCodeTest {

    @Test
    public void encodeDecodeTest() throws EncoderException, DecoderException {

        String text = "icefrog编码器";
        String strPunyCode = PunyCode.encode(text);
        Assert.assertEquals("icefrog-7v7l601w81o", strPunyCode);
        String decode = PunyCode.decode("icefrog-7v7l601w81o");
        Assert.assertEquals(text, decode);
        decode = PunyCode.decode("xn--icefrog-7v7l601w81o");
        Assert.assertEquals(text, decode);
    }
}
