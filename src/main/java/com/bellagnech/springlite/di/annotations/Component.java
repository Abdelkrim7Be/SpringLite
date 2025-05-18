package com.bellagnech.springlite.di.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a "component".
 * Such classes are considered as candidates for auto-detection
 * when using annotation-based configuration and classpath scanning.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    
    /**
     * The value may indicate a bean name suggestion.
     * If not specified, the lowercase non-qualified class name
     * will be used as the bean name.
     * 
     * @return the suggested bean name, if any
     */
    String value() default "";
}
