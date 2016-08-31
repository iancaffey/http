package com.iancaffey.http;

import java.io.IOException;

/**
 * HttpHandler
 *
 * @author Ian Caffey
 * @since 1.0
 */
public interface HttpHandler {
    public void accept(HttpReader reader, HttpWriter writer) throws IOException;
}
