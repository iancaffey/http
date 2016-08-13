package com.iancaffey.http.io;

/**
 * RequestVisitor
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
     * The {@code RequestVisitor} is responsible for writing the response out and closing the {@code ResponseWriter} to
     * complete the response for the request.
     *
     * @param writer the response writer
     * @throws Exception indicating there was an error writing out the response
     */
    public void respond(ResponseWriter writer) throws Exception;
}
