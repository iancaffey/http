package com.iancaffey.http.io;

/**
 * RequestVisitor
 * <p>
 * An object representing a visitor for an HTTP request.
 * <p>
 * As the request data is being processed, the data is sent in sequence to a {@code RequestVisitor}.
 * <p>
 * The order of operations goes as follows:
 * - visitRequest(String, String, String);
 * - visitHeader(String, String) (0 or many times)
 * - respond(HttpWriter) (generate response to request)
 * <p>
 * During the respond method, the request body can be read using the {@code HttpWriter} read method.
 * <p>
 * Using the Content-Length header can perform the request body read in a single non-blocking call, which is preferred.
 * <p>
 * Closing the HttpWriter is pertinent to ensuring the response is sent in a properly formatted fashion to the requester.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public interface RequestVisitor {
    /**
     * Visits the beginning header entry that details out request type, uri, and HTTP version.
     *
     * @param requestType the request type
     * @param uri         the uri
     * @param version     the HTTP version
     */
    public void visitRequest(String requestType, String uri, String version);

    /**
     * Visits a request header entry.
     *
     * @param key   the request header key
     * @param value the request header value
     */
    public void visitHeader(String key, String value);

    /**
     * Responds to a HTTP request. Responses are not restricted to be done within the caller thread.
     * <p>
     * The {@code RequestVisitor} is responsible for writing the response out and closing the {@code HttpWriter} to
     * complete the response for the request.
     *
     * @param writer the response writer
     * @throws Exception indicating there was an error writing out the response
     */
    public void respond(HttpWriter writer) throws Exception;
}
