package com.iancaffey.http.io;

import com.iancaffey.http.HttpServer;
import com.iancaffey.http.util.ResponseCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ResponseWriter
 * <p>
 * An object providing convenience methods for writing out response headers and formatting the response message.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class ResponseWriter extends PrintStream {
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
    private final InputStream in;

    /**
     * Constructs a new {@code ResponseWriter} with a specified {@code InputStream} and {@code OutputStream}.
     * <p>
     * Both streams are required as the {@code ResponseWriter} is responsible for cleaning up the streams once the response
     * has been successfully written to the {@code OutputStream}.
     *
     * @param in  the input stream
     * @param out the output stream
     */
    public ResponseWriter(InputStream in, OutputStream out) {
        super(out);
        if (in == null)
            throw new IllegalArgumentException();
        this.in = in;
    }


    /**
     * Writes out the "Content-Length" header entry to the {@code OutputStream}, appending "\r\n".
     *
     * @param length the content-length header entry value
     */
    public void printContentLength(long length) {
        printHeader(ResponseWriter.CONTENT_LENGTH, length);
    }

    /**
     * Writes out the "Content-Type" header entry to the {@code OutputStream}, appending "\r\n".
     *
     * @param type the content-type header entry value
     */
    public void printContentType(String type) {
        printHeader(ResponseWriter.CONTENT_TYPE, type);
    }

    /**
     * Writes out the "Date" header entry to the {@code OutputStream}, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the date header entry value
     */
    public void printDate(Instant instant) {
        printHeader(ResponseWriter.DATE, instant == null ? null :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Expires" header entry to the {@code OutputStream}, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the expires header entry value
     */
    public void printExpiration(Instant instant) {
        printHeader(ResponseWriter.EXPIRES, instant == null ? "Never" :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Last-modified" header entry to the {@code OutputStream}, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the last-modified header entry value
     */
    public void printLastModified(Instant instant) {
        printHeader(ResponseWriter.LAST_MODIFIED, instant == null ? "Never" :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Server" header entry to the {@code OutputStream}, appending "\r\n".
     *
     * @param server the server header entry value
     */
    public void printServer(String server) {
        printHeader(ResponseWriter.SERVER, server);
    }

    /**
     * Writes out the response code header entry to the {@code OutputStream}, appending "\r\n".
     *
     * @param code the response code
     */
    public void printResponseCode(ResponseCode code) {
        if (code == null)
            throw new IllegalArgumentException();
        printHeader(HttpServer.HTTP_VERSION + " " + code.value() + " " + code.message());
    }

    /**
     * Writes out a header entry to the {@code OutputStream}, appending "\r\n".
     * <p>
     * The header entry value is written using {@code String.valueOf(Object)}.
     *
     * @param key   the header entry key
     * @param value the header entry value
     */
    public void printHeader(String key, Object value) {
        printHeader(key + ": " + String.valueOf(value));
    }

    /**
     * Writes out a header entry to the {@code OutputStream}, appending "\r\n".
     *
     * @param key   the header entry key
     * @param value the header entry value
     */
    public void printHeader(String key, String value) {
        printHeader(key + ": " + value);
    }

    /**
     * Writes out a header entry to the {@code OutputStream}, appending "\r\n".
     *
     * @param header the header entry
     */
    public void printHeader(String header) {
        print(header + "\r\n");
    }

    /**
     * Writes out "\r\n" to the {@code OutputStream} which is required to end the response headers before writing out the body content.
     */
    public void endHeader() {
        print("\r\n");
    }

    /**
     * Closes both the {@code InputStream} and then the {@code OutputStream}.
     */
    @Override
    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.close();
    }

}
