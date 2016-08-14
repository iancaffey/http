package com.iancaffey.http;

import java.nio.channels.ReadableByteChannel;
import java.util.Map;

/**
 * Message
 * <p>
 * A compiled form of an HTTP message containing headers and a body.
 * <p>
 * The HTTP message is mutable, providing access to the direct message body and exposed methods for updating header values.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Message {
    /**
     * Default HTTP version used when creating HTTP messages.
     */
    public static final String HTTP_VERSION = "HTTP/1.1";
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
    private final String version;
    private final Map<String, String> headers;
    private ReadableByteChannel body;
    private long length;

    /**
     * Constructs a new {@code Message} with specified headers and no body.
     *
     * @param version the HTTP version in the format HTTP/x.x
     * @param headers the HTTP message headers
     */
    public Message(String version, Map<String, String> headers) {
        this(version, headers, null, 0);
    }

    /**
     * Constructs a new {@code Message} with specified headers and body.
     * <p>
     * Unknown body length is denoted by a length of -1.
     *
     * @param version the HTTP version in the format HTTP/x.x
     * @param headers the HTTP message headers
     * @param body    the HTTP message body
     * @param length  the HTTP message body length
     */
    public Message(String version, Map<String, String> headers, ReadableByteChannel body, long length) {
        if (version == null || headers == null)
            throw new IllegalArgumentException();
        this.version = version;
        this.headers = headers;
        body(body, length);
    }

    /**
     * Returns the HTTP version in the format HTTP/x.x.
     *
     * @return the HTTP version
     */
    public String version() {
        return version;
    }

    /**
     * Updates the value for the header entry at the specified key or creates a new one if it does not exist.
     *
     * @param key   the header entry key
     * @param value the header entry value
     */
    public void header(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Removes the header entry value at the specified key.
     *
     * @param key the header entry key
     */
    public void remove(String key) {
        headers.remove(key);
    }

    /**
     * Returns the header entry value at the specified key.
     *
     * @param key the header entry key
     * @return the value at the specified key, or {@code null} if it does not exist
     */
    public String header(String key) {
        return headers.get(key);
    }

    /**
     * Returns whether or not there exists a header entry with the specified key.
     *
     * @param key the header entry key
     * @return {@code true} if there exists a header entry with the specified key
     */
    public boolean hasHeader(String key) {
        return headers.containsKey(key);
    }

    /**
     * Returns a direct reference to the headers map.
     *
     * @return a direct reference to the headers map
     */
    public Map<String, String> headers() {
        return headers;
    }

    /**
     * Returns the message body as a {@code ReadableByteChannel}.
     *
     * @return the message body
     */
    public ReadableByteChannel body() {
        return body;
    }

    /**
     * Updates the message body.
     * <p>
     * An attempt is made to use the {@code Message.CONTENT_LENGTH} header as the body length.
     * If there is no {@code Message.CONTENT_LENGTH} header, a value of -1 is used to denote an unknown method body length.
     *
     * @param body the message body
     */
    public void body(ReadableByteChannel body) {
        body(body, hasHeader(Message.CONTENT_LENGTH) ? Long.parseLong(header(Message.CONTENT_LENGTH)) : 0);
    }

    /**
     * Updates the message body.
     * <p>
     * Unknown body length is denoted by a length of -1.
     *
     * @param body   the message body
     * @param length the length of the message body
     */
    public void body(ReadableByteChannel body, long length) {
        this.body = body;
        this.length = length;
        if (body == null) {
            headers.remove(Message.CONTENT_LENGTH);
        } else {
            headers.put(Message.CONTENT_LENGTH, String.valueOf(length));
        }
    }

    /**
     * Returns the message body length.
     *
     * @return the message body length
     */
    public long length() {
        return length;
    }
}
