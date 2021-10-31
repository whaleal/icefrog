package com.whaleal.icefrog.core.annotation.pojo;

import java.lang.annotation.*;

/**
 * An annotation that configures the discriminator key and value for a class.
 * 只能作用于类上 用于添加该实体类的 类型
 * 添加该标记 时 帮助解析该类的 具体生成对象
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Discriminator {

    /**
     * @return the discriminator value to use for this type.
     */
    String value() default "";

    /**
     * @return the discriminator key to use for this type.
     */
    String key() default "_t";
}
