package com.bellagnech.springlite.di.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When used as a type-level annotation in conjunction with {@link Component},
 * {@code @Scope} indicates the scope of the component.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    
    /**
     * Specifies the scope to use for the annotated component.
     * 
     * @return the specified scope
     */
    String value() default "singleton";
}
