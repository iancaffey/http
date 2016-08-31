package com.iancaffey.http;

import com.iancaffey.http.util.HttpHeader;
import com.iancaffey.http.util.ResponseCode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HttpWriter
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class HttpWriter extends BufferedWriter {
    /**
     * Constructs a new {@code HttpWriter} for the output stream.
     *
     * @param out the output stream
     */
    public HttpWriter(OutputStream out) {
        super(new OutputStreamWriter(out));
    }

    /**
     * Writes out the "Content-Length" header entry to the response output stream, appending "\r\n".
     *
     * @param length the content-length header entry value
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeContentLength(long length) throws IOException {
        writeHeader(HttpHeader.CONTENT_LENGTH, length);
    }

    /**
     * Writes out the "Content-Type" header entry to the response output stream, appending "\r\n".
     *
     * @param type the content-type header entry value
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeContentType(String type) throws IOException {
        writeHeader(HttpHeader.CONTENT_TYPE, type);
    }

    /**
     * Writes out the "Date" header entry to the response output stream, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the date header entry value
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeDate(Instant instant) throws IOException {
        writeHeader(HttpHeader.DATE, instant == null ? null :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Expires" header entry to the response output stream, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the expires header entry value
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeExpiration(Instant instant) throws IOException {
        writeHeader(HttpHeader.EXPIRES, instant == null ? "Never" :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Last-modified" header entry to the response output stream, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the last-modified header entry value
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeLastModified(Instant instant) throws IOException {
        writeHeader(HttpHeader.LAST_MODIFIED, instant == null ? "Never" :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Server" header entry to the response output stream, appending "\r\n".
     *
     * @param server the server header entry value
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeServer(String server) throws IOException {
        writeHeader(HttpHeader.SERVER, server);
    }

    /**
     * Writes out the response header entry to the response output stream, appending "\r\n".
     *
     * @param version the HTTP version
     * @param code    the response code
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeResponseHeader(String version, ResponseCode code) throws IOException {
        if (code == null)
            throw new IllegalArgumentException();
        writeHeader(version + " " + code.value() + " " + code.message());
    }

    /**
     * Writes out the request header entry to the response output stream, appending "\r\n".
     *
     * @param requestType the request type
     * @param uri         the request uri
     * @param version     the HTTP version
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeRequestHeader(String requestType, String uri, String version) throws IOException {
        writeHeader(requestType + " " + uri + " " + version);
    }

    /**
     * Writes out a header entry to the response output stream, appending "\r\n".
     * <p>
     * The header entry value is written using {@code String.valueOf(Object)}.
     *
     * @param key   the header entry key
     * @param value the header entry value
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeHeader(String key, Object value) throws IOException {
        writeHeader(key + ":" + String.valueOf(value));
    }

    /**
     * Writes out a header entry to the response output stream, appending "\r\n".
     *
     * @param key   the header entry key
     * @param value the header entry value
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeHeader(String key, String value) throws IOException {
        writeHeader(key + ":" + value);
    }

    /**
     * Writes out a header entry to the response output stream, appending "\r\n".
     *
     * @param header the header entry
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void writeHeader(String header) throws IOException {
        write(header + "\r\n");
    }

    /**
     * Writes out "\r\n" to the response output stream which is required to end the response headers before writing out the body content.
     *
     * @throws IOException indicating an error occurred while writing out to the output stream
     */
    public void endHeader() throws IOException {
        write("\r\n");
    }
}
