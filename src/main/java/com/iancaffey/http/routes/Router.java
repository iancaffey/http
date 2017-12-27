package com.iancaffey.http.routes;

import com.iancaffey.http.HttpHandler;
import com.iancaffey.http.HttpReader;
import com.iancaffey.http.HttpWriter;
import com.iancaffey.http.util.RoutePath;
import com.iancaffey.http.util.RoutingException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

/**
 * Router
 * <p>
 * An object that routes incoming HTTP requests to an appropriate route for response.
 * <p>
 * Router is thread-safe, making use of thread-locals to ensure the selected {@code Route}
 * when visiting the request data is consistent until when {@code Router#response(HttpWriter)} is called.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Router implements HttpHandler {
    private final Map<String, List<Route>> routes = new HashMap<>();

    /**
     * Constructs a new {@code Router} converting the methods annotated with {@code Get}, {@code Post}, and {@code Delete} to routes.
     * <p>
     * A method subject to becoming a {@code Route} must be annotated with {@code Get}, {@code Post}, or {@code Delete}.
     * <p>
     * The method return type must be a {@code HttpHandler}.
     * <p>
     * Each class is restricted to containing static routes.
     * <p>
     * Use {@code Router.asRouter(Controller...)} for already instantiated controllers.
     *
     * @param classes the classes containing annotated route methods
     * @return a new {@code Router}
     */
    public static Router asRouter(Class<? extends Controller>... classes) {
        if (classes == null)
            throw new IllegalArgumentException();
        Router router = new Router();
        for (Class<? extends Controller> c : classes)
            Router.addRoutes(c, router);
        return router;
    }

    /**
     * Constructs a new {@code Router} converting the methods annotated with {@code Get}, {@code Post}, and {@code Delete} to routes.
     * <p>
     * A method subject to becoming a {@code Route} must be annotated with {@code Get}, {@code Post}, or {@code Delete}.
     * <p>
     * The method return type must be a {@code HttpHandler}.
     *
     * @param controllers the controllers containing annotated route methods
     * @return a new {@code Router}
     */
    public static Router asRouter(Controller... controllers) {
        if (controllers == null)
            throw new IllegalArgumentException();
        Router router = new Router();
        for (Controller controller : controllers)
            Router.addRoutes(controller.getClass(), controller, router);
        return router;
    }

    /**
     * Converts the methods annotated with {@code Get}, {@code Post}, and {@code Delete} to routes and adds them to the specified router.
     * <p>
     * A method subject to becoming a {@code Route} must be annotated with {@code Get}, {@code Post}, or {@code Delete}.
     * <p>
     * The method return type must be a {@code HttpHandler}.
     * <p>
     * Each class is restricted to containing static routes.
     * <p>
     * Use {@code Router.addRoutes(Controller, Router)} for already instantiated controllers.
     *
     * @param c      the controller class
     * @param router the target router
     */
    public static void addRoutes(Class<? extends Controller> c, Router router) {
        Router.addRoutes(c, null, router);
    }

    /**
     * Converts the methods annotated with {@code Get}, {@code Post}, and {@code Delete} to routes and adds them to the specified router.
     * <p>
     * A method subject to becoming a {@code Route} must be annotated with {@code Get}, {@code Post}, or {@code Delete}.
     * <p>
     * The method return type must be a {@code HttpHandler}.
     *
     * @param controller the controller instance (if there are instance methods representing routes)
     * @param router     the target router
     */
    public static void addRoutes(Controller controller, Router router) {
        if (controller == null)
            throw new IllegalArgumentException();
        Router.addRoutes(controller.getClass(), controller, router);
    }

    /**
     * Converts the methods annotated with {@code Get}, {@code Post}, and {@code Delete} to routes and adds them to the specified router.
     * <p>
     * A method subject to becoming a {@code Route} must be annotated with {@code Get}, {@code Post}, or {@code Delete}.
     * <p>
     * The method return type must be a {@code HttpHandler}.
     *
     * @param controllerClass the controller class
     * @param controller      the controller instance (if there are instance methods representing routes)
     * @param router          the target router
     */
    private static void addRoutes(Class<? extends Controller> controllerClass, Controller controller, Router router) {
        if (controllerClass == null || router == null)
            throw new IllegalArgumentException();
        Class c = controllerClass;
        do {
            Stream.concat(Arrays.stream(c.getMethods()), Arrays.stream(c.getDeclaredMethods())).forEach(method -> {
                String requestType;
                String path;
                String[] patterns;
                int[] indexes;
                if (method.isAnnotationPresent(Get.class)) {
                    Get get = method.getAnnotation(Get.class);
                    requestType = HttpReader.GET;
                    path = get.value();
                    patterns = get.patterns();
                    indexes = get.indexes();
                } else if (method.isAnnotationPresent(Post.class)) {
                    Post post = method.getAnnotation(Post.class);
                    requestType = HttpReader.POST;
                    path = post.value();
                    patterns = post.patterns();
                    indexes = post.indexes();
                } else if (method.isAnnotationPresent(Delete.class)) {
                    Delete delete = method.getAnnotation(Delete.class);
                    requestType = HttpReader.DELETE;
                    path = delete.value();
                    patterns = delete.patterns();
                    indexes = delete.indexes();
                } else if(method.isAnnotationPresent(Options.class)){
                	Options options = method.getAnnotation(Options.class);
                    requestType = HttpReader.OPTIONS;
                    path = options.value();
                    patterns = options.patterns();
                    indexes = options.indexes();
                } else {
                    return;
                }
                String[] parameters = RoutePath.parameters(path);
                if (patterns.length != 0 && patterns.length != parameters.length)
                    throw new IllegalArgumentException("Parameter mismatch. A pattern must be specified for all parameters, if any.");
                if (indexes.length != 0 && indexes.length != parameters.length)
                    throw new IllegalArgumentException("Parameter mismatch. An index must be specified for all parameters, if any.");
                Route route = new Route(requestType, path);
                for (int i = 0; i < patterns.length; i++)
                    route.where(parameters[i], patterns[i]);
                for (int i = 0; i < indexes.length; i++)
                    route.where(parameters[i], indexes[i]);
                boolean isStatic = Modifier.isStatic(method.getModifiers());
                if (!isStatic && controller == null)
                    throw new IllegalArgumentException("Illegal route. Methods must be declared static for non-instantiated controllers.");
                route.use(method, isStatic ? null : controller);
                router.add(route);
            });
        } while ((c = c.getSuperclass()) != null);
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
        Route route = Route.get(path);
        add(route);
        return route;
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
        Route route = Route.post(path);
        add(route);
        return route;
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
        Route route = Route.delete(path);
        add(route);
        return route;
    }

    /**
     * Adds a new route to the router.
     *
     * @param route the route
     * @return {@code this} for method-chaining
     */
    public Router add(Route route) {
        if (route == null)
            throw new IllegalArgumentException();
        String requestType = route.requestType();
        if (routes.containsKey(requestType))
            routes.get(requestType).add(route);
        else {
            List<Route> routes = new ArrayList<>();
            routes.add(route);
            this.routes.put(requestType, routes);
        }
        return this;
    }

    /**
     * Locates the route that matches the incoming request uri.
     * <p>
     * A route must be an absolute match to the request uri to be considered.
     *
     * @param uri the uri
     * @return the best route that matched the uri
     * @throws RoutingException indicating a route could not be found that matches the request type and uri pair
     */
    public Route find(String requestType, String uri) {
        if (!routes.containsKey(requestType))
            throw new RoutingException("Unable to find route. Request type: " + requestType + ", uri: " + uri);
        List<Route> routes = this.routes.get(requestType);
        for (Route route : routes)
            if (route.matches(uri))
                return route;
        throw new RoutingException("Unable to find route. Request type: " + requestType + ", uri: " + uri);
    }

    /**
     * Accepts an incoming HTTP exchange, represented by a {@code HttpReader} for parsing the incoming request and a {@code HttpWriter} for writing out a response.
     *
     * @param reader the HTTP reader for parsing the incoming HTTP request
     * @param writer the HTTP writer for writing out the HTTP response
     * @throws IOException indicating an error occurred while reading in the request or writing out the responsez
     */
    @Override
    public void accept(HttpReader reader, HttpWriter writer) throws IOException {
        try {
            Object o = find(reader.readRequestType(), reader.readUri()).invoke(reader.readUri());
            if (!(o instanceof HttpHandler))
                throw new RoutingException("Route return type mismatch. Expected: HttpHandler, Actual: " + (o == null ? null : o.getClass()));
            ((HttpHandler) o).accept(reader, writer);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke route action.", e);
        }
    }
}
