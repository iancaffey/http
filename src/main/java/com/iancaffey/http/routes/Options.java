package com.iancaffey.http.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Options
 *
 * @author zhangj
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Options {
    public String value();
    public String[] patterns() default {};
    public int[] indexes() default {};
}
