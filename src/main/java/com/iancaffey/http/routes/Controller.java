package com.iancaffey.http.routes;

import com.iancaffey.http.HttpHandler;
import com.iancaffey.http.HttpServer;
import com.iancaffey.http.util.ResponseCode;

/**
 * Controller
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Controller {
    //TODO:Create base class for static http handlers that Controller extends
    public static HttpHandler code(ResponseCode code) {
        return (reader, writer) -> {
            writer.writeResponseHeader(HttpServer.VERSION, code);
            writer.endHeader();
            writer.flush();
            writer.close();
        };
    }
}
