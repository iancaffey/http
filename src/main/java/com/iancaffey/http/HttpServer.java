package com.iancaffey.http;

import com.iancaffey.http.routes.Controller;
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

    /**
     * Default HTTP version used when creating HTTP messages.
     */
    public static final String VERSION = "HTTP/1.1";

    private final Router router;

    private HttpServer(ExchangeFactory factory, Router router) {
        super(factory, new HttpExchangeHandler(router));
        this.router = router;
    }

    public static HttpServer bind(int port) throws IOException {
        return new HttpServer(new SocketExchangeFactory(new ServerSocket(port)), new Router());
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
     * The method return type must be a {@code HttpHandler}.
     * <p>
     * Each class is restricted to containing static routes.
     * <p>
     * Use {@code accept(Controller)} for already instantiated controllers.
     *
     * @param c the controller class
     */
    public HttpServer accept(Class<? extends Controller> c) {
        Router.addRoutes(c, router);
        return this;
    }

    /**
     * Converts the methods annotated with {@code Get}, {@code Post}, and {@code Delete} to routes and adds them to the router.
     * <p>
     * A method subject to becoming a {@code Route} must be annotated with {@code Get}, {@code Post}, or {@code Delete}.
     * <p>
     * The method return type must be a {@code HttpHandler}.
     *
     * @param controller the controller instance (if there are instance methods representing routes)
     */
    public HttpServer accept(Controller controller) {
        Router.addRoutes(controller, router);
        return this;
    }
}
