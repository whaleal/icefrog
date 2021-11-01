package com.whaleal.icefrog.crypto.digest.mac;

import com.whaleal.icefrog.crypto.SmUtil;
import com.whaleal.icefrog.crypto.digest.HmacAlgorithm;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

/**
 * {@link MacEngine} 实现工厂类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class MacEngineFactory {

    /**
     * 根据给定算法和密钥生成对应的{@link MacEngine}
     *
     * @param algorithm 算法，见{@link HmacAlgorithm}
     * @param key       密钥
     * @return {@link MacEngine}
     */
    public static MacEngine createEngine( String algorithm, Key key ) {
        return createEngine(algorithm, key, null);
    }

    /**
     * 根据给定算法和密钥生成对应的{@link MacEngine}
     *
     * @param algorithm 算法，见{@link HmacAlgorithm}
     * @param key       密钥
     * @param spec      spec
     * @return {@link MacEngine}
     * @since 1.0.0
     */
    public static MacEngine createEngine( String algorithm, Key key, AlgorithmParameterSpec spec ) {
        if (algorithm.equalsIgnoreCase(HmacAlgorithm.HmacSM3.getValue())) {
            // HmacSM3算法是BC库实现的，忽略加盐
            return SmUtil.createHmacSm3Engine(key.getEncoded());
        }
        return new DefaultHMacEngine(algorithm, key, spec);
    }
}
