package com.whaleal.icefrog.core.annotation.pojo;

import java.lang.annotation.*;

/**
 * An annotation that configures the discriminator key and value for a class.
 * 只能作用于类上 用于添加该实体类的 类型
 * 添加该标记 时 帮助解析该类的 具体生成对象
 * 主要有两部分
 * key  用于与数据库侧进行交互
 * value 为保存在实体名称
 *
 *
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Discriminator {
    /**
     * 用于标记 是否开启 Discriminator
     * @return 开启标记
     */
    boolean useDiscriminator() default false;

    /**
     * @return the discriminator value to use for this type.
     * 与数据库交互存储的值
     */
    String value() default "";

    /**
     * @return the discriminator key to use for this type.
     * 与数据库交互 存储的字段名称
     */
    String key() default "";
}
