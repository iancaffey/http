package com.iancaffey.http;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Request
 * <p>
 * A compiled form of an HTTP request left over from {@code RequestVisitor} visiting the header.
 * <p>
 * Request headers are mutable as the visiting of the header is a two-step process: visiting the first line defining the
 * type, uri, and version, and then stepping through each header entry.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Request {
    /**
     * HTTP request type for "GET". Requests data from a specified resource.
     */
    public static final String GET = "GET";
    /**
     * HTTP request type for "POST". Submits data to be processed to a specified resource.
     */
    public static final String POST = "POST";
    /**
     * HTTP request type for "DELETE". Deletes the specified resource.
     */
    public static final String DELETE = "DELETE";
    private final String type;
    private final String uri;
    private final String version;
    private Map<String, String> headers;

    /**
     * Constructs a new {@code Request} given complete header information.
     *
     * @param type    the request type
     * @param uri     the uri
     * @param version the HTTP version
     * @param headers the request headers
     */
    public Request(String type, String uri, String version, Map<String, String> headers) {
        this.type = type;
        this.uri = uri;
        this.version = version;
        this.headers = headers;
    }

    /**
     * Returns the request type.
     *
     * @return the request type
     */
    public String type() {
        return type;
    }

    /**
     * Returns the request uri.
     *
     * @return the request uri
     */
    public String uri() {
        return uri;
    }

    /**
     * Returns the HTTP version used in the request.
     *
     * @return the request's HTTP version
     */
    public String version() {
        return version;
    }

    /**
     * Updates the value for the header entry at the specified key or creates a new one if it does not exist.
     *
     * @param key   the header entry key
     * @param value the header entry value
     * @return {@code this} for method-chaining
     */
    public Request header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * Returns the header entry value at the specified key.
     *
     * @param key the header entry key
     * @return the value at the specified key, or {@code null} if it does not exist
     */
    public String header(String key) {
        return headers.get(key);
    }

    /**
     * Returns a copy of the entire headers map.
     * <p>
     * Original insertion order is maintained through the copy. The copy is mutable.
     *
     * @return a copy of the entire headers map
     */
    public Map<String, String> headers() {
        return new LinkedHashMap<>(headers);
    }
}
