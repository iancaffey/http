package com.iancaffey.http.routes;

import com.iancaffey.http.io.ResponseWriter;

/**
 * Route
 * <p>
 * An object representing an HTTP endpoint, writing out response data using {@code ResponseWriter}.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public interface Route {
    /**
     * Writes out the response headers and message using the {@code ResponseWriter}.
     *
     * @param writer the response writer
     */
    public void apply(ResponseWriter writer);
}
