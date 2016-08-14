package com.iancaffey.http.routes;

import com.iancaffey.http.Response;
import com.iancaffey.http.io.HttpWriter;
import com.iancaffey.http.io.RequestVisitor;
import com.iancaffey.http.util.RoutingException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Router
 * <p>
 * An object that routes incoming HTTP requests to an appropriate routing table for response.
 * <p>
 * Router is thread-safe, making use of a thread-local response to ensure the {@code Route} located within the routing table
 * when visiting the request data is consistent until when {@code Router#response(HttpWriter)} is called.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Router implements RequestVisitor {
    private final Map<String, RoutingTable> requestTypeTables = new ConcurrentHashMap<>();
    private final ThreadLocal<RoutingTable> selected = new ThreadLocal<>();

    /**
     * Constructs a new {@code Router} with a {@code RoutingTable} already registered.
     *
     * @param table the routing table to register
     * @return a new {@code Router}
     */
    public static Router of(RoutingTable table) {
        return new Router().register(table);
    }

    /**
     * Registers the routing table to be eligible for future requests.
     *
     * @param table the routing table
     * @return {@code this} for method-chaining
     */
    public Router register(RoutingTable table) {
        if (table == null)
            throw new IllegalArgumentException();
        requestTypeTables.put(table.requestType(), table);
        return this;
    }

    /**
     * Locates the {@code Response} within the appropriate {@code RoutingTable} for the uri.
     *
     * @param requestType the request type
     * @param uri         the uri
     * @return the best route that matched the uri
     * @see RoutingTable#find(String)
     */
    public Response route(String requestType, String uri) {
        return requestTypeTables.containsKey(requestType) ? requestTypeTables.get(requestType).find(uri) : null;
    }

    /**
     * Visits the beginning header entry that details out request type, uri, and HTTP version.
     * <p>
     * Using the request type and uri, the best {@code Route} is located and stored for use within {@code Router#response(HttpWriter)}.
     *
     * @param requestType the request type
     * @param uri         the uri
     * @param version     the HTTP version
     */
    @Override
    public void visitRequest(String requestType, String uri, String version) {
        RoutingTable table = requestTypeTables.get(requestType);
        table.visitRequest(requestType, uri, version);
        selected.set(table);
    }

    /**
     * Ignores all header value-pairs.
     *
     * @param key   the header key
     * @param value the header value
     */
    @Override
    public void visitHeader(String key, String value) {
        RoutingTable table = selected.get();
        if (table == null)
            throw new RoutingException("No active request being handled.");
        table.visitHeader(key, value);
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
        RoutingTable table = selected.get();
        if (table == null)
            throw new RoutingException("No active request being handled.");
        table.respond(writer);
    }
}
