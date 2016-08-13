package com.iancaffey.http.routes;

import com.iancaffey.http.model.Response;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * RoutingTable
 * <p>
 * An object representing all possible response paths for a request type. Both direct and pattern-based responses can be added.
 * Direct responses use a equals check against the given URI for selection.
 * Pattern-based responses use Java Regular Expressions to find a full match on the given URI for selection.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class RoutingTable {
    private final String requestType;
    private final Map<String, Response> directResponses;
    private final Map<Pattern, Response> patternResponses;

    /**
     * Constructs a new {@code RoutingTable} for a specific request type with given direct responses and pattern responses.
     *
     * @param requestType      the request type
     * @param directResponses  the direct/literal response paths
     * @param patternResponses the pattern-based response paths
     */
    public RoutingTable(String requestType, Map<String, Response> directResponses, Map<Pattern, Response> patternResponses) {
        if (requestType == null || directResponses == null || patternResponses == null)
            throw new IllegalArgumentException();
        this.requestType = requestType;
        this.directResponses = directResponses;
        this.patternResponses = patternResponses;
    }

    /**
     * Constructs a new {@code RoutingTable} for a specific request type with a single direct response provided.
     *
     * @param requestType the request type
     * @param path        the direct/literal response path
     * @param response    the response
     * @return a new {@code RoutingTable}
     */
    public static RoutingTable of(String requestType, String path, Response response) {
        return new RoutingTable(requestType, Collections.singletonMap(path, response), Collections.emptyMap());
    }

    /**
     * Constructs a new {@code RoutingTable} for a specific request type with a single pattern-based response provided.
     *
     * @param requestType the request type
     * @param pattern     the expression pattern
     * @param response    the response
     * @return a new {@code RoutingTable}
     */
    public static RoutingTable of(String requestType, Pattern pattern, Response response) {
        return new RoutingTable(requestType, Collections.emptyMap(), Collections.singletonMap(pattern, response));
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
     * Locates the best response that matches the uri.
     * <p>
     * Direct responses are searched first, and then each pattern-based response is checked for a full match against the uri.
     * <p>
     * If no direct responses are found and there are no full-match pattern-based responses, an attempt to find a partial match
     * for a pattern-based response will be made.
     *
     * @param uri the uri
     * @return the best response that matched the uri
     */
    public Response find(String uri) {
        if (directResponses.containsKey(uri))
            return directResponses.get(uri);
        for (Map.Entry<Pattern, Response> entry : patternResponses.entrySet())
            if (entry.getKey().matcher(uri).matches())
                return entry.getValue();
        for (Map.Entry<Pattern, Response> entry : patternResponses.entrySet())
            if (entry.getKey().matcher(uri).find())
                return entry.getValue();
        return null;
    }
}
