package com.iancaffey.http.routes;

import com.iancaffey.http.HttpHandler;
import com.iancaffey.http.HttpServer;
import com.iancaffey.http.util.RoutePath;
import com.iancaffey.http.util.RoutingException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Route
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Route {
    public static final String DEFAULT_PARAMETER_PATTERN = ".+";
    public static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{([A-z]+)}");
    private final String path;
    private final List<String> parameters = new ArrayList<>();
    private final Map<Integer, String> reverseParameterOrder = new HashMap<>();
    private final Map<String, Integer> parameterOrder = new HashMap<>();
    private final Map<String, String> parameterPatterns = new HashMap<>();
    private final String requestType;
    private HttpHandler direct;
    private Method method;
    private Pattern evaluatedPattern;
    private Object parent;

    public Route(String requestType, String path) {
        if (requestType == null || path == null)
            throw new IllegalArgumentException();
        this.requestType = requestType;
        this.path = path;
        Matcher matcher = PARAMETER_PATTERN.matcher(path);
        int i = 0;
        while (matcher.find()) {
            String parameter = matcher.group(1);
            parameters.add(parameter);
            parameterOrder.put(parameter, i);
            reverseParameterOrder.put(i, parameter);
            parameterPatterns.put(parameter, DEFAULT_PARAMETER_PATTERN);
            i++;
        }
    }

    public static Route get(String path) {
        return new Route(HttpServer.GET, path);
    }

    public static Route post(String path) {
        return new Route(HttpServer.POST, path);
    }

    public static Route delete(String path) {
        return new Route(HttpServer.DELETE, path);
    }

    public String requestType() {
        return requestType;
    }

    public String path() {
        return path;
    }

    public Route where(String parameter, String pattern) {
        if (parameter == null || pattern == null)
            throw new IllegalArgumentException();
        if (!parameterPatterns.containsKey(parameter))
            throw new IllegalArgumentException("Unknown parameter: " + parameter);
        parameterPatterns.put(parameter, pattern);
        evaluatedPattern = null;
        return this;
    }

    public Route where(String parameter, int index) {
        if (!parameterOrder.containsKey(parameter))
            throw new IllegalArgumentException("Unknown parameter: " + parameter);
        int original = parameterOrder.get(parameter);
        if (original == index)
            return this;
        String previous = reverseParameterOrder.get(index);
        parameterOrder.put(previous, original);
        reverseParameterOrder.put(original, previous);
        parameterOrder.put(parameter, index);
        reverseParameterOrder.put(index, parameter);
        return this;
    }

    public Route use(HttpHandler handler) {
        this.direct = handler;
        return this;
    }

    public Route use(String methodPath) {
        return use(methodPath, null);
    }

    public Route use(Method method) {
        return use(method, null);
    }

    public Route use(String methodPath, Object parent) {
        try {
            return use(RoutePath.of(methodPath, RoutePath.parameterCount(path)), parent);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find target method.", e);
        }
    }

    public Route use(Method method, Object parent) {
        this.method = method;
        this.parent = parent;
        if (method != null && !HttpHandler.class.isAssignableFrom(method.getReturnType()))
            throw new RoutingException("Routes must return a HttpHandler. Actual: " + method.getReturnType());
        return this;
    }

    public boolean matches(String uri) {
        if (uri == null)
            throw new IllegalArgumentException();
        return (evaluatedPattern != null ? evaluatedPattern : (evaluatedPattern = compile())).matcher(uri).matches();
    }

    public Object invoke(String uri) throws InvocationTargetException, IllegalAccessException {
        if (direct != null)
            return direct;
        if (method == null)
            throw new RoutingException("No method configured for route. Route#use must be called to assign the method to invoke.");
        method.setAccessible(true);
        Matcher matcher = (evaluatedPattern != null ? evaluatedPattern : (evaluatedPattern = compile())).matcher(uri);
        if (!matcher.matches())
            throw new RoutingException("Unable to match uri against route pattern.");
        if (matcher.groupCount() != parameters.size())
            throw new RoutingException("Parameter mismatch. Unable to find matcher group for each argument.");
        if (parameters.isEmpty())
            return method.invoke(parent);
        Class<?>[] types = method.getParameterTypes();
        Object[] values = new Object[parameters.size()];
        for (int i = 0; i < values.length; i++) {
            String s = matcher.group(i + 1);
            int index = parameterOrder.get(parameters.get(i));
            Object value = null;
            Class<?> c = types[i];
            if (c == String.class)
                value = s;
            else if (c == int.class || c == Integer.class)
                value = Integer.parseInt(s);
            else if (c == long.class || c == Long.class)
                value = Long.parseLong(s);
            else if (c == float.class || c == Float.class)
                value = Float.parseFloat(s);
            else if (c == double.class || c == Double.class)
                value = Double.parseDouble(s);

            values[index] = value;
        }
        return method.invoke(parent, values);
    }

    public Pattern compile() {
        StringBuilder builder = new StringBuilder();
        int lastStart;
        int lastEnd = 0;
        while ((lastStart = path.indexOf('{', lastEnd)) != -1) {
            if (lastStart == 0)
                throw new RoutingException("Missing beginning / in route path: " + path);
            if (lastStart == path.length() - 1)
                throw new RoutingException("Malformed route path. Unclosed parameter name: " + path);
            if (lastStart != lastEnd + 1)
                builder.append(Pattern.quote(path.substring(lastEnd, lastStart)));
            int closingIndex = path.indexOf('}', lastStart);
            String pattern = parameterPatterns.get(path.substring(lastStart + 1, closingIndex));
            if (!pattern.startsWith("(") && !pattern.endsWith(")"))
                builder.append("(").append(pattern).append(")");
            else
                builder.append(pattern);
            lastEnd = closingIndex;
        }
        if (lastEnd == 0)
            builder.append(path);
        return Pattern.compile(builder.toString());
    }
}
