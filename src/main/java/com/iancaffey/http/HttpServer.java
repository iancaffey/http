package com.iancaffey.http;

import com.iancaffey.http.io.RequestVisitor;

/**
 * HttpServer
 * <p>
 * An object representing a basic HTTP server.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public interface HttpServer extends AutoCloseable {
    /**
     * Accepts a {@code RequestVisitor} to be used on the next incoming HTTP request.
     * <p>
     * The method will block until a new request (or a previously enqueued request) can be found.
     * Processing the request does not have to be done synchronously.
     * The implementation of {@code HttpServer} is free to take advantage of any multi-threading.
     *
     * @param visitor the visitor to process the incoming HTTP request
     */
    public void accept(RequestVisitor visitor) throws Exception;
}
