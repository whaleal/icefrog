
package com.whaleal.icefrog.core.annotation.pojo;


import java.lang.annotation.*;

/**
 * 实体类标记
 * 主要用于修改相关表名
 *
 * @author wh
 *
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Entity {

    String value() default "";


}
