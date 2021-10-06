package com.whaleal.icefrog.extra.template;

import com.whaleal.icefrog.core.lang.Dict;
import com.whaleal.icefrog.core.util.CharsetUtil;
import com.whaleal.icefrog.extra.template.engine.velocity.VelocityEngine;
import org.junit.Assert;
import org.junit.Test;

public class VelocityTest {

	@Test
	public void charsetTest(){
		final TemplateConfig config = new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH);
		config.setCustomEngine(VelocityEngine.class);
		config.setCharset(CharsetUtil.CHARSET_GBK);
		final TemplateEngine engine = TemplateUtil.createEngine(config);
		Template template = engine.getTemplate("velocity_test_gbk.vtl");
		String result = template.render(Dict.create().set("name", "icefrog"));
		Assert.assertEquals("你好,icefrog", result);
	}
}
