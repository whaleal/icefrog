
package com.whaleal.icefrog.core.annotation.pojo;

import java.lang.annotation.*;


/**
 *
 * 用于标记一个中的构造方法
 * 只能作用于方法 或构造方法
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Constructor {
}
