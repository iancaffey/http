package com.iancaffey.http;

import com.iancaffey.http.routes.Route;
import com.iancaffey.http.routes.Router;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * HttpServer
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class HttpServer extends Server {
    private final Router router;
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
    /**
     * Default HTTP version used when creating HTTP messages.
     */
    public static final String VERSION = "HTTP/1.1";
    /**
     * Response header "Content-Length".
     */
    public static final String CONTENT_LENGTH = "Content-Length";
    /**
     * Response header "Content-Type".
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * Response header "Date".
     */
    public static final String DATE = "Date";
    /**
     * Response header "Expires".
     */
    public static final String EXPIRES = "Expires";
    /**
     * Response header "Last-modified".
     */
    public static final String LAST_MODIFIED = "Last-modified";
    /**
     * Response header "Server".
     */
    public static final String SERVER = "Server";

    public static HttpServer bind(int port) throws IOException {
        return new HttpServer(new SocketExchangeFactory(new ServerSocket(port)), new Router());
    }

    private HttpServer(ExchangeFactory factory, Router router) {
        super(factory, new HttpExchangeHandler(router));
        this.router = router;
    }

    /**
     * Creates a new {@code Route} with a "GET" request type and the specified path.
     * <p>
     * The method returns the newly created route to allow customizing the routes parameter ordering and patterns.
     *
     * @param path the route path
     * @return the newly created route
     */
    public Route get(String path) {
        return router.get(path);
    }

    /**
     * Creates a new {@code Route} with a "POST" request type and the specified path.
     * <p>
     * The method returns the newly created route to allow customizing the routes parameter ordering and patterns.
     *
     * @param path the route path
     * @return the newly created route
     */
    public Route post(String path) {
        return router.post(path);
    }

    /**
     * Creates a new {@code Route} with a "DELETE" request type and the specified path.
     * <p>
     * The method returns the newly created route to allow customizing the routes parameter ordering and patterns.
     *
     * @param path the route path
     * @return the newly created route
     */
    public Route delete(String path) {
        return router.delete(path);
    }

    /**
     * Converts the methods annotated with {@code Get}, {@code Post}, and {@code Delete} to routes and adds them to the router.
     * <p>
     * A method subject to becoming a {@code Route} must be annotated with {@code Get}, {@code Post}, or {@code Delete}.
     * <p>
     * The method return type must be either a {@code Response} for static routes or {@code HttpExchange} for routes that
     * have access to the incoming request and generate dynamic content.
     * <p>
     * Each class is restricted to containing static routes.
     * <p>
     * Use {@code accept(Class, Object)} for already instantiated controllers.
     *
     * @param c the controller class
     */
    public void accept(Class<?> c) {
        Router.addRoutes(c, router);
    }

    /**
     * Converts the methods annotated with {@code Get}, {@code Post}, and {@code Delete} to routes and adds them to the router.
     * <p>
     * A method subject to becoming a {@code Route} must be annotated with {@code Get}, {@code Post}, or {@code Delete}.
     * <p>
     * The method return type must be either a {@code Response} for static routes or {@code HttpExchange} for routes that
     * have access to the incoming request and generate dynamic content.
     *
     * @param c the controller class
     * @param o the controller instance (if there are instance methods representing routes)
     */
    public void addRoutes(Class<?> c, Object o) {
        Router.addRoutes(c, o, router);
    }
}
