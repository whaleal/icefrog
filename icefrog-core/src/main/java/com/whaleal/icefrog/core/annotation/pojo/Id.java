package com.whaleal.icefrog.core.annotation.pojo;

import java.lang.annotation.*;

/**
 * An annotation that configures the property as the id property .
 *
 * @author wh
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Id {
}
