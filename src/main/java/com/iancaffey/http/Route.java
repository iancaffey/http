package com.iancaffey.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

/**
 * Route
 * <p>
 * An object providing information on an endpoint.
 * <p>
 * Routes can be either direct or value-based, like how the {@code Router} and {@code RoutingTable} is implemented, however
 * the request type and path/value are done individually per route rather than creating sets of routes for each request type.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    /**
     * Pattern used for identifying route paths that cannot be used for absolute path matching.
     */
    public static final Pattern ROUTE_PATTERN = Pattern.compile("(\\{.+})");

    /**
     * Returns the request type for the route.
     *
     * @return the route request type
     */
    public String requestType() default Request.GET;

    /**
     * Returns the path value for the route.
     * <p>
     * <p>
     * If any regular expression characters are located within the route path, the value will be automatically interpreted as
     * a pattern. Inspection is done using {@code Route.ROUTE_PATTERN}.
     * <p>
     * For pattern-based routes, the value will be compiled using {@code URIPattern.compile(String}.
     * A complete match will be attempted and fallback to a partial match.
     * <p>
     * For absolute routes, the value will be used for a complete check against a uri.
     *
     * @return the direct route path
     */
    public String value() default "";
}
