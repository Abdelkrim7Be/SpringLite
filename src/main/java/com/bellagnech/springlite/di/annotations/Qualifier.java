package com.bellagnech.springlite.di.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation may be used on a field or parameter as a qualifier for
 * candidate beans when autowiring. It can also be used to annotate other
 * custom annotations that can then be used as qualifiers.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {
    
    /**
     * The qualifier value.
     * 
     * @return the qualifier value
     */
    String value() default "";
}
