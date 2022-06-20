package com.whaleal.icefrog.extra.template.engine.beetl;

import com.whaleal.icefrog.extra.template.AbstractTemplate;

import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;

/**
 * Beetl模板实现
 *
 * @author looly
 */
public class BeetlTemplate extends AbstractTemplate implements Serializable {
    private static final long serialVersionUID = -8157926902932567280L;

    private final org.beetl.core.Template rawTemplate;

    /**
     * 构造
     *
     * @param beetlTemplate Beetl的模板对象 {@link org.beetl.core.Template}
     */
    public BeetlTemplate( org.beetl.core.Template beetlTemplate ) {
        this.rawTemplate = beetlTemplate;
    }

    /**
     * 包装Beetl模板
     *
     * @param beetlTemplate Beetl的模板对象 {@link org.beetl.core.Template}
     * @return BeetlTemplate
     */
    public static BeetlTemplate wrap( org.beetl.core.Template beetlTemplate ) {
        return (null == beetlTemplate) ? null : new BeetlTemplate(beetlTemplate);
    }

    @Override
    public void render( Map<?, ?> bindingMap, Writer writer ) {
        rawTemplate.binding(bindingMap);
        rawTemplate.renderTo(writer);
    }

    @Override
    public void render( Map<?, ?> bindingMap, OutputStream out ) {
        rawTemplate.binding(bindingMap);
        rawTemplate.renderTo(out);
    }

}
