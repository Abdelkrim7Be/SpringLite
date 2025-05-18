package com.bellagnech.springlite.di.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a constructor, field, or method as to be autowired by the dependency injection framework.
 * This means that the annotated dependency will be automatically injected.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    
    /**
     * Declares whether the annotated dependency is required.
     * <p>Defaults to {@code true}.
     * 
     * @return whether the dependency is required
     */
    boolean required() default true;
}
