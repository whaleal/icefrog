package com.whaleal.icefrog.script.test;

import com.whaleal.icefrog.core.io.resource.ResourceUtil;
import com.whaleal.icefrog.script.ScriptRuntimeException;
import com.whaleal.icefrog.script.ScriptUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * 脚本单元测试类
 *
 * @author Looly
 * @author wh
 *
 */
public class ScriptUtilTest {

	@Test
	public void compileTest() {
		CompiledScript script = ScriptUtil.compile("print('Script test!');");
		try {
			script.eval();
		} catch (ScriptException e) {
			throw new ScriptRuntimeException(e);
		}
	}

	@Test
	public void evalTest() {
		ScriptUtil.eval("print('Script test!');");
	}

	@Test
	public void invokeTest() {
		final Object result = ScriptUtil.invoke(ResourceUtil.readUtf8Str("filter1.js"), "filter1", 2, 1);
		Assert.assertTrue((Boolean) result);
	}

	@Test
	public void pythonTest() throws ScriptException {
		final ScriptEngine pythonEngine = ScriptUtil.getPythonEngine();
		pythonEngine.eval("print('Hello Python')");
	}

	@Test
	public void luaTest() throws ScriptException {
		final ScriptEngine engine = ScriptUtil.getLuaEngine();
		engine.eval("print('Hello Lua')");
	}

	@Test
	public void groovyTest() throws ScriptException {
		final ScriptEngine engine = ScriptUtil.getGroovyEngine();
		engine.eval("println 'Hello Groovy'");
	}
}
