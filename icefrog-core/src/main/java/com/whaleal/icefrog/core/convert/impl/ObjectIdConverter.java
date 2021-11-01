package com.whaleal.icefrog.core.convert.impl;

import com.whaleal.icefrog.core.convert.AbstractConverter;
import com.whaleal.icefrog.core.lang.ObjectId;


/**
 * ObjectId对象转换器转换器
 *
 * @author wh
 * @since 1.1.0
 */
public class ObjectIdConverter extends AbstractConverter<ObjectId> {
    private static final long serialVersionUID = 1L;

    @Override
    protected ObjectId convertInternal( Object value ) {
        if (value instanceof ObjectId) {
            return (ObjectId) value;
        } else if (value instanceof String) {

            try {
                return new ObjectId((String) value);
            } catch (Exception e) {
                return null;
            }
        }
        return null;

    }

}

