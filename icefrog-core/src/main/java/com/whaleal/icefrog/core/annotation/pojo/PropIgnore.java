package com.whaleal.icefrog.core.annotation.pojo;

import java.lang.annotation.*;

/**
 * 属性忽略注解，使用此注解的字段等会被忽略，主要用于Bean拷贝、Bean转Map等<br>
 * 此注解应用于字段时，忽略读取和设置属性值，应用于setXXX方法忽略设置值，应用于getXXX忽略读取值
 * <p>
 * 不可作用于类上
 * 作用于方法，字段时忽略该 方法，字段
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface PropIgnore {

}
