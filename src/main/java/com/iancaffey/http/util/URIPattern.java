package com.iancaffey.http.util;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * URIPattern
 * <p>
 * A lightweight utility class replicating {@code Pattern} static method functionality while escaping patterns to be
 * capable of matching URI values.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class URIPattern {
    private URIPattern() {

    }

    /**
     * Escapes the query string and compiles the given regular expression into a pattern.
     *
     * @param pattern The expression to be compiled
     * @return the given regular expression compiled into a pattern
     * @throws PatternSyntaxException indicating the expression's syntax is invalid
     */
    public static Pattern compile(String pattern) {
        return Pattern.compile(URIPattern.escape(pattern));
    }

    /**
     * Returns a {@code Pattern} string capable of using core Java regular expressions with a query string.
     * <p>
     * The question mark trailing the initial forward slash is escaped to treat it as a literal value.
     *
     * @param pattern the uri pattern
     * @return an escaped uri pattern
     */
    public static String escape(String pattern) {
        return pattern.replace("/?", "/\\\\?");
    }
}
