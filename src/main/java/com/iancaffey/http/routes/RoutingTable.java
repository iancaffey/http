package com.iancaffey.http.routes;

import com.iancaffey.http.Response;
import com.iancaffey.http.io.HttpWriter;
import com.iancaffey.http.io.RequestVisitor;
import com.iancaffey.http.util.RoutingException;

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
public class RoutingTable implements RequestVisitor {
    private final String requestType;
    private final Map<String, Response> directResponses;
    private final Map<Pattern, Response> patternResponses;
    private final ThreadLocal<Response> selected = new ThreadLocal<>();

    /**
     * Constructs a new {@code RoutingTable} for a specific request type with a single direct response provided.
     *
     * @param requestType the request type
     * @param path        the direct/literal response path
     * @param response    the response
     */
    public RoutingTable(String requestType, String path, Response response) {
        this(requestType, Collections.singletonMap(path, response), Collections.emptyMap());
    }

    /**
     * Constructs a new {@code RoutingTable} for a specific request type with a single pattern-based response provided.
     *
     * @param requestType the request type
     * @param pattern     the expression pattern
     * @param response    the response
     */
    public RoutingTable(String requestType, Pattern pattern, Response response) {
        this(requestType, Collections.emptyMap(), Collections.singletonMap(pattern, response));
    }

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


    /**
     * Visits the beginning header entry that details out request type, uri, and HTTP version.
     * <p>
     * Using the uri, the best {@code Response} is located and stored for use within {@code Response#respond(HttpWriter)}.
     *
     * @param requestType the request type
     * @param uri         the uri
     * @param version     the HTTP version
     */
    @Override
    public void visitRequest(String requestType, String uri, String version) {
        selected.set(find(uri));
    }

    /**
     * Ignores all header value-pairs.
     *
     * @param key   the header key
     * @param value the header value
     */
    @Override
    public void visitHeader(String key, String value) {

    }

    /**
     * Responds to a HTTP request. Responses are not restricted to be done within the caller thread.
     * <p>
     * The {@code RequestVisitor} is responsible for writing the response out and closing the {@code HttpWriter} to
     * complete the response for the request.
     * <p>
     * If the route was successfully located, it will be applied to the {@code HttpWriter} and afterwards, the writer
     * will be closed. It is pertinent that the Route maintains thread-safety and external multi-threading be implemented
     * as the {@code Router} will close the writer before the {@code Route} has finished writing out the response.
     *
     * @param writer the response writer
     * @throws Exception indicating there was an error writing out the response or the route could not be found
     */
    @Override
    public void respond(HttpWriter writer) throws Exception {
        Response response = selected.get();
        if (response == null)
            throw new RoutingException("Unable to find response.");
        response.apply(writer);
    }
}
