package com.iancaffey.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Route
 * <p>
 * An object providing information on an endpoint.
 * <p>
 * Routes can be either direct or pattern-based, like how the {@code Router} and {@code RoutingTable} is implemented, however
 * the request type and path/pattern are done individually per route rather than creating sets of routes for each request type.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    /**
     * Returns the request type for the route.
     *
     * @return the route request type
     */
    public String requestType() default Request.GET;

    /**
     * Returns the direct path for the route.
     * <p>
     * The direct path is optional, if a non-empty value is provided for the route pattern.
     *
     * @return the direct route path
     */
    public String path() default "";

    /**
     * Returns the path pattern for the route.
     * <p>
     * Route patterns are used by default if a non-empty value is provided. If the route pattern is empty, a non-empty
     * value must be provided for the direct path.
     *
     * @return the direct route path
     */
    public String pattern() default "";
}
