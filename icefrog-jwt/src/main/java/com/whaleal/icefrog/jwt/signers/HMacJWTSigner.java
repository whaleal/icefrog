package com.whaleal.icefrog.jwt.signers;

import com.whaleal.icefrog.core.util.CharsetUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.crypto.digest.HMac;

import java.nio.charset.Charset;
import java.security.Key;

/**
 * HMac算法签名实现
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class HMacJWTSigner implements JWTSigner {

    private final HMac hMac;
    private Charset charset = CharsetUtil.CHARSET_UTF_8;

    /**
     * 构造
     *
     * @param algorithm HMAC签名算法
     * @param key       密钥
     */
    public HMacJWTSigner( String algorithm, byte[] key ) {
        this.hMac = new HMac(algorithm, key);
    }

    /**
     * 构造
     *
     * @param algorithm HMAC签名算法
     * @param key       密钥
     */
    public HMacJWTSigner( String algorithm, Key key ) {
        this.hMac = new HMac(algorithm, key);
    }

    /**
     * 设置编码
     *
     * @param charset 编码
     * @return 编码
     */
    public HMacJWTSigner setCharset( Charset charset ) {
        this.charset = charset;
        return this;
    }

    @Override
    public String sign( String headerBase64, String payloadBase64 ) {
        return hMac.digestBase64(StrUtil.format("{}.{}", headerBase64, payloadBase64), charset, true);
    }

    @Override
    public boolean verify( String headerBase64, String payloadBase64, String signBase64 ) {
        final String sign = sign(headerBase64, payloadBase64);
        return hMac.verify(
                StrUtil.bytes(sign, charset),
                StrUtil.bytes(signBase64, charset));
    }

    @Override
    public String getAlgorithm() {
        return this.hMac.getAlgorithm();
    }
}
