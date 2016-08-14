package com.iancaffey.http;

import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
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
public class Request extends Message {
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

    /**
     * Constructs a new {@code Request} given complete header information and no message body.
     *
     * @param type    the request type
     * @param uri     the uri
     * @param version the HTTP version
     * @param headers the request headers
     */
    public Request(String type, String uri, String version, Map<String, String> headers) {
        this(type, uri, version, headers, null, 0);
    }

    /**
     * Constructs a new {@code Request} given complete header information and a message body.
     * <p>
     * Unknown body length is denoted by a length of -1.
     *
     * @param type    the request type
     * @param uri     the uri
     * @param version the HTTP version
     * @param headers the request headers
     * @param body    the request body
     * @param length  the request body length
     */
    public Request(String type, String uri, String version, Map<String, String> headers, ReadableByteChannel body, long length) {
        super(version, headers, body, length);
        this.type = type;
        this.uri = uri;
        this.version = version;
        if (Request.GET.equals(type))
            remove(Message.CONTENT_LENGTH);
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
     * Returns the query string.
     * <p>
     * The beginning question mark '?' is removed.
     *
     * @return the query string.
     */
    public String query() {
        int index = uri.indexOf('?');
        return index == -1 || index == uri.length() - 1 ? null : uri.substring(index + 1);
    }

    /**
     * Returns the request parameters.
     * <p>
     * If the request type is {@code Request.GET}, the parameters are the parsed query string.
     *
     * @return the request parameters
     */
    public Map<String, String> parameters() {
        switch(type){
            case Request.GET:
                String query = query();
                if(query == null)
                    return Collections.emptyMap();
                String[] parameters = query.split("&");
                return null;
            default:
                throw new UnsupportedOperationException("Parameter parsing is unsupported for request type: " + type);
        }
    }

    /**
     * Returns the HTTP version used in the request.
     *
     * @return the request's HTTP version
     */
    public String version() {
        return version;
    }
}
