package com.iancaffey.http.io;

import com.iancaffey.http.HttpServer;
import com.iancaffey.http.util.ResponseCode;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HttpWriter
 * <p>
 * An object providing convenience methods for writing out request/response headers and processing message data.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class HttpWriter extends ChannelWriter {
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

    /**
     * Constructs a new {@code HttpWriter} with a specified {@code ByteChannel}.
     *
     * @param channel the channel
     */
    public HttpWriter(ByteChannel channel) {
        super(channel);
    }

    /**
     * Constructs a new {@code HttpWriter} with a specified byte channels.
     *
     * @param in  the input channel
     * @param out the output channel
     */
    public HttpWriter(ReadableByteChannel in, WritableByteChannel out) {
        super(in, out);
    }

    /**
     * Writes out the "Content-Length" header entry to the {@code Channel}, appending "\r\n".
     *
     * @param length the content-length header entry value
     */
    public void writeContentLength(long length) throws IOException {
        writeHeader(HttpWriter.CONTENT_LENGTH, length);
    }

    /**
     * Writes out the "Content-Type" header entry to the {@code Channel}, appending "\r\n".
     *
     * @param type the content-type header entry value
     */
    public void writeContentType(String type) throws IOException {
        writeHeader(HttpWriter.CONTENT_TYPE, type);
    }

    /**
     * Writes out the "Date" header entry to the {@code Channel}, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the date header entry value
     */
    public void writeDate(Instant instant) throws IOException {
        writeHeader(HttpWriter.DATE, instant == null ? null :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Expires" header entry to the {@code Channel}, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the expires header entry value
     */
    public void writeExpiration(Instant instant) throws IOException {
        writeHeader(HttpWriter.EXPIRES, instant == null ? "Never" :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Last-modified" header entry to the {@code Channel}, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the last-modified header entry value
     */
    public void writeLastModified(Instant instant) throws IOException {
        writeHeader(HttpWriter.LAST_MODIFIED, instant == null ? "Never" :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Server" header entry to the {@code Channel}, appending "\r\n".
     *
     * @param server the server header entry value
     */
    public void writeServer(String server) throws IOException {
        writeHeader(HttpWriter.SERVER, server);
    }

    /**
     * Writes out the response code header entry to the {@code Channel}, appending "\r\n".
     *
     * @param code the response code
     */
    public void writeResponseCode(ResponseCode code) throws IOException {
        if (code == null)
            throw new IllegalArgumentException();
        writeHeader(HttpServer.HTTP_VERSION + " " + code.value() + " " + code.message());
    }

    /**
     * Writes out a header entry to the {@code Channel}, appending "\r\n".
     * <p>
     * The header entry value is written using {@code String.valueOf(Object)}.
     *
     * @param key   the header entry key
     * @param value the header entry value
     */
    public void writeHeader(String key, Object value) throws IOException {
        writeHeader(key + ": " + String.valueOf(value));
    }

    /**
     * Writes out a header entry to the {@code Channel}, appending "\r\n".
     *
     * @param key   the header entry key
     * @param value the header entry value
     */
    public void writeHeader(String key, String value) throws IOException {
        writeHeader(key + ": " + value);
    }

    /**
     * Writes out a header entry to the {@code Channel}, appending "\r\n".
     *
     * @param header the header entry
     */
    public void writeHeader(String header) throws IOException {
        write(header + "\r\n");
    }

    /**
     * Writes out "\r\n" to the {@code Channel} which is required to end the response headers before writing out the body content.
     */
    public void endHeader() throws IOException {
        write("\r\n");
    }
}
