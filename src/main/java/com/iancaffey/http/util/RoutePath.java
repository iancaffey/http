package com.iancaffey.http.util;

import com.iancaffey.http.routes.Route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * RoutePath
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class RoutePath {
    private RoutePath() {

    }

    public static int parameterCount(String path) {
        if (path == null)
            throw new IllegalArgumentException();
        Matcher matcher = Route.PARAMETER_PATTERN.matcher(path);
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

    public static String[] parameters(String path) {
        if (path == null)
            throw new IllegalArgumentException();
        List<String> parameters = new ArrayList<>();
        Matcher matcher = Route.PARAMETER_PATTERN.matcher(path);
        int count = 0;
        while (matcher.find())
            parameters.add(matcher.group(1));
        return parameters.toArray(new String[parameters.size()]);
    }

    public static Method of(String methodPath) throws ClassNotFoundException {
        return RoutePath.of(methodPath, 0);
    }

    public static Method of(String methodPath, int parameterCount) throws ClassNotFoundException {
        int index;
        if (methodPath == null || (index = methodPath.indexOf('@')) == -1 || index == methodPath.length() - 1)
            throw new IllegalArgumentException("Method path must be in the format ClassName@methodName");
        return RoutePath.of(methodPath.substring(0, index), methodPath.substring(index + 1), parameterCount);
    }

    public static Method of(String parent, String method) throws ClassNotFoundException {
        return RoutePath.of(ClassLoader.getSystemClassLoader().loadClass(parent), method);
    }

    public static Method of(String parent, String method, int parameterCount) throws ClassNotFoundException {
        return RoutePath.of(ClassLoader.getSystemClassLoader().loadClass(parent), method, parameterCount);
    }

    public static Method of(Class<?> parent, String method) {
        return RoutePath.of(parent, method, 0);
    }

    public static Method of(Class<?> parent, String method, int parameterCount) {
        if (parent == null || method == null || parameterCount < 0)
            throw new IllegalArgumentException();
        Class<?> c = parent;
        do {
            for (Method m : c.getMethods())
                if (method.equals(m.getName()) && m.getParameterCount() == parameterCount)
                    return m;
            for (Method m : c.getDeclaredMethods())
                if (method.equals(m.getName()) && m.getParameterCount() == parameterCount)
                    return m;
        } while ((c = c.getSuperclass()) != null);
        throw new RoutingException(
                String.format("Unable to find method mapping to route[parent=%s, method=%s, parameterCount=%s].",
                        parent, method, parameterCount));
    }
}
