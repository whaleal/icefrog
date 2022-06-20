package com.whaleal.icefrog.extra.template.engine.thymeleaf;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.extra.template.Template;
import com.whaleal.icefrog.extra.template.TemplateConfig;
import com.whaleal.icefrog.extra.template.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.*;

/**
 * Thymeleaf模板引擎实现
 *
 * @author looly
 */
public class ThymeleafEngine implements TemplateEngine {

    org.thymeleaf.TemplateEngine engine;
    TemplateConfig config;

    // --------------------------------------------------------------------------------- Constructor start

    /**
     * 默认构造
     */
    public ThymeleafEngine() {
    }

    /**
     * 构造
     *
     * @param config 模板配置
     */
    public ThymeleafEngine( TemplateConfig config ) {
        init(config);
    }

    /**
     * 构造
     *
     * @param engine {@link org.thymeleaf.TemplateEngine}
     */
    public ThymeleafEngine( org.thymeleaf.TemplateEngine engine ) {
        init(engine);
    }
    // --------------------------------------------------------------------------------- Constructor end

    /**
     * 创建引擎
     *
     * @param config 模板配置
     * @return {@link TemplateEngine}
     */
    private static org.thymeleaf.TemplateEngine createEngine( TemplateConfig config ) {
        if (null == config) {
            config = new TemplateConfig();
        }

        ITemplateResolver resolver;
        switch (config.getResourceMode()) {
            case CLASSPATH:
                final ClassLoaderTemplateResolver classLoaderResolver = new ClassLoaderTemplateResolver();
                classLoaderResolver.setCharacterEncoding(config.getCharsetStr());
                classLoaderResolver.setTemplateMode(TemplateMode.HTML);
                classLoaderResolver.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), "/"));
                resolver = classLoaderResolver;
                break;
            case FILE:
                final FileTemplateResolver fileResolver = new FileTemplateResolver();
                fileResolver.setCharacterEncoding(config.getCharsetStr());
                fileResolver.setTemplateMode(TemplateMode.HTML);
                fileResolver.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), "/"));
                resolver = fileResolver;
                break;
            case WEB_ROOT:
                final FileTemplateResolver webRootResolver = new FileTemplateResolver();
                webRootResolver.setCharacterEncoding(config.getCharsetStr());
                webRootResolver.setTemplateMode(TemplateMode.HTML);
                webRootResolver.setPrefix(StrUtil.addSuffixIfNot(FileUtil.getAbsolutePath(FileUtil.file(FileUtil.getWebRoot(), config.getPath())), "/"));
                resolver = webRootResolver;
                break;
            case STRING:
                resolver = new StringTemplateResolver();
                break;
            default:
                resolver = new DefaultTemplateResolver();
                break;
        }

        final org.thymeleaf.TemplateEngine engine = new org.thymeleaf.TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    @Override
    public TemplateEngine init( TemplateConfig config ) {
        if (null == config) {
            config = TemplateConfig.DEFAULT;
        }
        this.config = config;
        init(createEngine(config));
        return this;
    }

    /**
     * 初始化引擎
     *
     * @param engine 引擎
     */
    private void init( org.thymeleaf.TemplateEngine engine ) {
        this.engine = engine;
    }

    @Override
    public Template getTemplate( String resource ) {
        if (null == this.engine) {
            init(TemplateConfig.DEFAULT);
        }
        return ThymeleafTemplate.wrap(this.engine, resource, (null == this.config) ? null : this.config.getCharset());
    }
}
