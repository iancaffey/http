package com.iancaffey.http.util;

import com.iancaffey.http.HttpServer;
import com.iancaffey.http.HttpHandler;

/**
 * Response
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Response {
    private Response() {

    }

    public static HttpHandler code(ResponseCode code) {
        return (reader, writer) -> {
            writer.writeResponseHeader(HttpServer.VERSION, code);
            writer.endHeader();
            writer.flush();
            writer.close();
        };
    }
}
