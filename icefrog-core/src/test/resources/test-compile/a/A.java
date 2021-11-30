package a;

import com.whaleal.icefrog.core.lang.ConsoleTable;
import com.whaleal.icefrog.core.lang.caller.CallerUtil;

public class A {
    public A() {
        new InnerClass() {{
            int i = 0;
            Class<?> caller = CallerUtil.getCaller(i);
            final ConsoleTable t = new ConsoleTable();
            t.addHeader("类名", "类加载器");
            System.out.println("初始化 " + getClass() + " 的调用链为: ");
            while (caller != null) {
                System.out.println("caller 的 ClassLoader() 值为" + caller.getClassLoader());
                t.addBody(caller.toString(), caller.getClassLoader() == null ? "null" : caller.getClassLoader().toString());
                caller = CallerUtil.getCaller(++i);
            }
            t.print();
        }};
    }

    private class InnerClass {
    }
}