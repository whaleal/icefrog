package com.whaleal.icefrog.core.lang.generator;

import com.whaleal.icefrog.core.lang.ObjectId;

/**
 * ObjectId生成器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class ObjectIdGenerator implements Generator<String> {
    @Override
    public String next() {
        return ObjectId.next();
    }
}
