package com.iancaffey.http.routes;

import com.iancaffey.http.util.URIPattern;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * RoutingTable
 * <p>
 * An object representing all possible route paths for a request type. Both direct and pattern-based routes can be added.
 * Direct routes use a equals check against the given URI for selection.
 * Pattern-based routes use Java Regular Expressions to find a full match on the given URI for selection.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class RoutingTable {
    private final String requestType;
    private final Map<String, Route> directRoutes;
    private final Map<Pattern, Route> patternRoutes;

    /**
     * Constructs a new {@code RoutingTable} for a specific request type with given direct routes and pattern routes.
     *
     * @param requestType   the request type
     * @param directRoutes  the direct/literal route paths
     * @param patternRoutes the pattern-based route paths
     */
    public RoutingTable(String requestType, Map<String, Route> directRoutes, Map<Pattern, Route> patternRoutes) {
        if (requestType == null || directRoutes == null || patternRoutes == null)
            throw new IllegalArgumentException();
        this.requestType = requestType;
        this.directRoutes = directRoutes;
        this.patternRoutes = patternRoutes;
    }

    /**
     * Constructs a new {@code RoutingTable} for a specific request type with a single direct route provided.
     *
     * @param requestType the request type
     * @param path        the direct/literal route path
     * @param route       the route
     * @return a new {@code RoutingTable}
     */
    public static RoutingTable singletonDirectRoute(String requestType, String path, Route route) {
        return new RoutingTable(requestType, Collections.singletonMap(path, route), Collections.emptyMap());
    }

    /**
     * Constructs a new {@code RoutingTable} for a specific request type with a single pattern-based route provided.
     * <p>
     * The specified Regular Expression pattern is compiled using {@code URIPattern.compile(String)}.
     *
     * @param requestType the request type
     * @param pattern     the expression pattern
     * @param route       the route
     * @return a new {@code RoutingTable}
     */
    public static RoutingTable singletonPatternRoute(String requestType, String pattern, Route route) {
        return RoutingTable.singletonPatternRoute(requestType, URIPattern.compile(pattern), route);
    }

    /**
     * Constructs a new {@code RoutingTable} for a specific request type with a single pattern-based route provided.
     *
     * @param requestType the request type
     * @param pattern     the expression pattern
     * @param route       the route
     * @return a new {@code RoutingTable}
     */
    public static RoutingTable singletonPatternRoute(String requestType, Pattern pattern, Route route) {
        return new RoutingTable(requestType, Collections.emptyMap(), Collections.singletonMap(pattern, route));
    }

    /**
     * Returns the request type for the routing table.
     *
     * @return the routing table request type
     */
    public String requestType() {
        return requestType;
    }

    /**
     * Locates the best route that matches the uri.
     * <p>
     * Direct routes are searched first, and then each pattern-based route is checked for a full match against the uri.
     * <p>
     * If no direct routes are found and there are no full-match pattern-based routes, an attempt to find a partial match
     * for a pattern-based route will be made.
     *
     * @param uri the uri
     * @return the best route that matched the uri
     */
    public Route find(String uri) {
        if (directRoutes.containsKey(uri))
            return directRoutes.get(uri);
        for (Map.Entry<Pattern, Route> entry : patternRoutes.entrySet())
            if (entry.getKey().matcher(uri).matches())
                return entry.getValue();
        for (Map.Entry<Pattern, Route> entry : patternRoutes.entrySet())
            if (entry.getKey().matcher(uri).find())
                return entry.getValue();
        return null;
    }
}
