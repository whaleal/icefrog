package com.whaleal.icefrog.core.annotation.pojo;


import java.lang.annotation.*;


/**
 * Optional annotation for specifying persistence behavior
 *
 * @author wh
 * @since 1.1
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Property {

    /**
     * 用于标识该类的引用
     * 可以用于复杂的类型，如内嵌对象等 用于反序列化的对象，适合子类等情况
     *
     * @return the concrete class to instantiate.
     */
    Class<?> concreteClass() default Object.class;

    /**
     * 用于标记字段别名
     *
     * @return the field name to use in the document.  Defaults to the java field name.
     */
    String value() default "";
}
