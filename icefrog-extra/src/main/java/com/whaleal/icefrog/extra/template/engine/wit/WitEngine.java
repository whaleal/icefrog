package com.whaleal.icefrog.extra.template.engine.wit;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.lang.Dict;
import com.whaleal.icefrog.extra.template.Template;
import com.whaleal.icefrog.extra.template.TemplateConfig;
import com.whaleal.icefrog.extra.template.TemplateEngine;
import com.whaleal.icefrog.extra.template.TemplateException;
import org.febit.wit.Engine;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.util.Props;

import java.io.File;

/**
 * Wit(http://zqq90.github.io/webit-script/)模板引擎封装
 *
 * @author Looly
 * @author wh
 */
public class WitEngine implements TemplateEngine {

    private Engine engine;

    // --------------------------------------------------------------------------------- Constructor start

    /**
     * 默认构造
     */
    public WitEngine() {
    }

    /**
     * 构造
     *
     * @param config 模板配置
     */
    public WitEngine( TemplateConfig config ) {
        init(config);
    }

    /**
     * 构造
     *
     * @param engine {@link Engine}
     */
    public WitEngine( Engine engine ) {
        init(engine);
    }
    // --------------------------------------------------------------------------------- Constructor end

    /**
     * 创建引擎
     *
     * @param config 模板配置
     * @return {@link Engine}
     */
    private static Engine createEngine( TemplateConfig config ) {
        final Props configProps = Engine.createConfigProps("");
        Dict dict = null;

        if (null != config) {
            dict = Dict.create();
            // 自定义编码
            dict.set("DEFAULT_ENCODING", config.getCharset());

            switch (config.getResourceMode()) {
                case CLASSPATH:
                    configProps.set("pathLoader.root", config.getPath());
                    configProps.set("routeLoader.defaultLoader", "classpathLoader");
                    break;
                case STRING:
                    configProps.set("routeLoader.defaultLoader", "stringLoader");
                    break;
                case FILE:
                    configProps.set("pathLoader.root", config.getPath());
                    configProps.set("routeLoader.defaultLoader", "fileLoader");
                    break;
                case WEB_ROOT:
                    final File root = FileUtil.file(FileUtil.getWebRoot(), config.getPath());
                    configProps.set("pathLoader.root", FileUtil.getAbsolutePath(root));
                    configProps.set("routeLoader.defaultLoader", "fileLoader");
                    break;
            }
        }

        return Engine.create(configProps, dict);
    }

    @Override
    public TemplateEngine init( TemplateConfig config ) {
        init(createEngine(config));
        return this;
    }

    /**
     * 初始化引擎
     *
     * @param engine 引擎
     */
    private void init( Engine engine ) {
        this.engine = engine;
    }

    @Override
    public Template getTemplate( String resource ) {
        if (null == this.engine) {
            init(TemplateConfig.DEFAULT);
        }
        try {
            return WitTemplate.wrap(engine.getTemplate(resource));
        } catch (ResourceNotFoundException e) {
            throw new TemplateException(e);
        }
    }
}
