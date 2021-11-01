package com.whaleal.icefrog.core.bean.copier.provider;

import com.whaleal.icefrog.core.bean.DynaBean;
import com.whaleal.icefrog.core.bean.copier.ValueProvider;
import com.whaleal.icefrog.core.convert.Convert;

import java.lang.reflect.Type;

/**
 * DynaBean值提供者
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class DynaBeanValueProvider implements ValueProvider<String> {

    private final DynaBean dynaBean;
    private final boolean ignoreError;

    /**
     * 构造
     *
     * @param dynaBean    DynaBean
     * @param ignoreError 是否忽略错误
     */
    public DynaBeanValueProvider( DynaBean dynaBean, boolean ignoreError ) {
        this.dynaBean = dynaBean;
        this.ignoreError = ignoreError;
    }

    @Override
    public Object value( String key, Type valueType ) {
        final Object value = dynaBean.get(key);
        return Convert.convertWithCheck(valueType, value, null, this.ignoreError);
    }

    @Override
    public boolean containsKey( String key ) {
        return dynaBean.containsProp(key);
    }

}
