package com.whaleal.icefrog.extra.template;

import com.whaleal.icefrog.core.lang.Dict;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.extra.template.TemplateConfig.ResourceMode;
import com.whaleal.icefrog.extra.template.engine.beetl.BeetlEngine;
import com.whaleal.icefrog.extra.template.engine.enjoy.EnjoyEngine;
import com.whaleal.icefrog.extra.template.engine.freemarker.FreemarkerEngine;
import com.whaleal.icefrog.extra.template.engine.rythm.RythmEngine;
import com.whaleal.icefrog.extra.template.engine.thymeleaf.ThymeleafEngine;
import com.whaleal.icefrog.extra.template.engine.velocity.VelocityEngine;
import com.whaleal.icefrog.extra.template.engine.wit.WitEngine;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板引擎单元测试
 *
 * @author Looly
 * @author wh
 */
public class TemplateUtilTest {

    @Test
    public void createEngineTest() {
        // 字符串模板, 默认模板引擎，此处为Beetl
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig());
        Template template = engine.getTemplate("hello,${name}");
        String result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", result);

        // classpath中获取模板
        engine = TemplateUtil.createEngine(new TemplateConfig("templates", ResourceMode.CLASSPATH));
        Template template2 = engine.getTemplate("beetl_test.btl");
        String result2 = template2.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", result2);
    }

    @Test
    public void beetlEngineTest() {
        // 字符串模板
        TemplateEngine engine = new BeetlEngine(new TemplateConfig("templates"));
        Template template = engine.getTemplate("hello,${name}");
        String result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", result);

        // classpath中获取模板
        engine = new BeetlEngine(new TemplateConfig("templates", ResourceMode.CLASSPATH));
        Template template2 = engine.getTemplate("beetl_test.btl");
        String result2 = template2.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", result2);
    }

    @Test
    public void rythmEngineTest() {
        // 字符串模板
        TemplateEngine engine = TemplateUtil.createEngine(
                new TemplateConfig("templates").setCustomEngine(RythmEngine.class));
        Template template = engine.getTemplate("hello,@name");
        String result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", result);

        // classpath中获取模板
        Template template2 = engine.getTemplate("rythm_test.tmpl");
        String result2 = template2.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", result2);
    }

    @Test
    public void freemarkerEngineTest() {
        // 字符串模板
        TemplateEngine engine = TemplateUtil.createEngine(
                new TemplateConfig("templates", ResourceMode.STRING).setCustomEngine(FreemarkerEngine.class));
        Template template = engine.getTemplate("hello,${name}");
        String result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", result);

        //ClassPath模板
        engine = TemplateUtil.createEngine(
                new TemplateConfig("templates", ResourceMode.CLASSPATH).setCustomEngine(FreemarkerEngine.class));
        template = engine.getTemplate("freemarker_test.ftl");
        result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", result);
    }

    @Test
    public void velocityEngineTest() {
        // 字符串模板
        TemplateEngine engine = TemplateUtil.createEngine(
                new TemplateConfig("templates", ResourceMode.STRING).setCustomEngine(VelocityEngine.class));
        Template template = engine.getTemplate("你好,$name");
        String result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("你好,icefrog", result);

        //ClassPath模板
        engine = TemplateUtil.createEngine(
                new TemplateConfig("templates", ResourceMode.CLASSPATH).setCustomEngine(VelocityEngine.class));
        template = engine.getTemplate("velocity_test.vtl");
        result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("你好,icefrog", result);

        template = engine.getTemplate("templates/velocity_test.vtl");
        result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("你好,icefrog", result);
    }

    @Test
    public void enjoyEngineTest() {
        // 字符串模板
        TemplateEngine engine = TemplateUtil.createEngine(
                new TemplateConfig("templates").setCustomEngine(EnjoyEngine.class));
        Template template = engine.getTemplate("#(x + 123)");
        String result = template.render(Dict.create().set("x", 1));
        Assert.assertEquals("124", result);

        //ClassPath模板
        engine = new EnjoyEngine(
                new TemplateConfig("templates", ResourceMode.CLASSPATH).setCustomEngine(EnjoyEngine.class));
        template = engine.getTemplate("enjoy_test.etl");
        result = template.render(Dict.create().set("x", 1));
        Assert.assertEquals("124", result);
    }

    @Test
    public void thymeleafEngineTest() {
        // 字符串模板
        TemplateEngine engine = TemplateUtil.createEngine(
                new TemplateConfig("templates").setCustomEngine(ThymeleafEngine.class));
        Template template = engine.getTemplate("<h3 th:text=\"${message}\"></h3>");
        String result = template.render(Dict.create().set("message", "icefrog"));
        Assert.assertEquals("<h3>icefrog</h3>", result);

        //ClassPath模板
        engine = TemplateUtil.createEngine(
                new TemplateConfig("templates", ResourceMode.CLASSPATH).setCustomEngine(ThymeleafEngine.class));
        template = engine.getTemplate("thymeleaf_test.ttl");
        result = template.render(Dict.create().set("message", "icefrog"));
        Assert.assertEquals("<h3>icefrog</h3>", result);
    }

    @Test
    @Ignore
    public void renderToFileTest() {
        TemplateEngine engine = new BeetlEngine(new TemplateConfig("templates", ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("freemarker_test.ftl");

        final Map<String, Object> bindingMap = new HashMap<>();
        bindingMap.put("name", "aa");
        File outputFile = new File("e:/test.txt");
        template.render(bindingMap, outputFile);
    }

    @Test
    public void WitEngineTest() {
        //classpath模板
        TemplateConfig config = new TemplateConfig("templates", ResourceMode.CLASSPATH)
                .setCustomEngine(WitEngine.class);
        TemplateEngine engine = TemplateUtil.createEngine(config);
        Template template = engine.getTemplate("/wit_test.wit");
        String result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", StrUtil.trim(result));

        // 字符串模板
        config = new TemplateConfig("templates", ResourceMode.STRING)
                .setCustomEngine(WitEngine.class);
        engine = TemplateUtil.createEngine(config);
        template = engine.getTemplate("<%var name;%>hello,${name}");
        result = template.render(Dict.create().set("name", "icefrog"));
        Assert.assertEquals("hello,icefrog", StrUtil.trim(result));
    }
}
