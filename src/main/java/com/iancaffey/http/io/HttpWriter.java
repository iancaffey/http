package com.iancaffey.http.io;

import com.iancaffey.http.Message;
import com.iancaffey.http.Request;
import com.iancaffey.http.Response;
import com.iancaffey.http.util.ResponseCode;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeContentLength(long length) throws IOException {
        writeHeader(Message.CONTENT_LENGTH, length);
    }

    /**
     * Writes out the "Content-Type" header entry to the {@code Channel}, appending "\r\n".
     *
     * @param type the content-type header entry value
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeContentType(String type) throws IOException {
        writeHeader(Message.CONTENT_TYPE, type);
    }

    /**
     * Writes out the "Date" header entry to the {@code Channel}, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the date header entry value
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeDate(Instant instant) throws IOException {
        writeHeader(Message.DATE, instant == null ? null :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Expires" header entry to the {@code Channel}, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the expires header entry value
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeExpiration(Instant instant) throws IOException {
        writeHeader(Message.EXPIRES, instant == null ? "Never" :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Last-modified" header entry to the {@code Channel}, appending "\r\n".
     * <p>
     * The date is formatted using {@code DateTimeFormatter.RFC_1123_DATE_TIME}.
     *
     * @param instant the last-modified header entry value
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeLastModified(Instant instant) throws IOException {
        writeHeader(Message.LAST_MODIFIED, instant == null ? "Never" :
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"))));
    }

    /**
     * Writes out the "Server" header entry to the {@code Channel}, appending "\r\n".
     *
     * @param server the server header entry value
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeServer(String server) throws IOException {
        writeHeader(Message.SERVER, server);
    }

    /**
     * Writes out the response header entry to the {@code Channel}, appending "\r\n".
     *
     * @param version the HTTP version
     * @param code    the response code
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeResponseHeader(String version, ResponseCode code) throws IOException {
        if (code == null)
            throw new IllegalArgumentException();
        writeHeader(version + " " + code.value() + " " + code.message());
    }

    /**
     * Writes out the request header entry to the {@code Channel}, appending "\r\n".
     *
     * @param requestType the request type
     * @param uri         the request uri
     * @param version     the HTTP version
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeRequestHeader(String requestType, String uri, String version) throws IOException {
        writeHeader(requestType + " " + uri + " " + version);
    }

    /**
     * Writes out a header entry to the {@code Channel}, appending "\r\n".
     * <p>
     * The header entry value is written using {@code String.valueOf(Object)}.
     *
     * @param key   the header entry key
     * @param value the header entry value
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeHeader(String key, Object value) throws IOException {
        writeHeader(key + ": " + String.valueOf(value));
    }

    /**
     * Writes out a header entry to the {@code Channel}, appending "\r\n".
     *
     * @param key   the header entry key
     * @param value the header entry value
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeHeader(String key, String value) throws IOException {
        writeHeader(key + ": " + value);
    }

    /**
     * Writes out a header entry to the {@code Channel}, appending "\r\n".
     *
     * @param header the header entry
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void writeHeader(String header) throws IOException {
        write(header + "\r\n");
    }

    /**
     * Writes out "\r\n" to the {@code Channel} which is required to end the response headers before writing out the body content.
     *
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void endHeader() throws IOException {
        write("\r\n");
    }

    /**
     * Writes out the request headers and message body.
     *
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void write(Request request) throws IOException {
        writeRequestHeader(request.type(), request.uri(), request.version());
        write((Message) request);
    }

    /**
     * Writes out the response headers and message body.
     *
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void write(Response response) throws IOException {
        writeResponseHeader(response.version(), response.code());
        write((Message) response);
    }

    /**
     * Writes out the HTTP message headers and message body.
     *
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public void write(Message message) throws IOException {
        for (Map.Entry<String, String> entry : message.headers().entrySet())
            writeHeader(entry.getKey(), entry.getValue());
        endHeader();
        ReadableByteChannel body = message.body();
        if (body == null)
            return;
        if (message.hasHeader(Message.CONTENT_LENGTH)) {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.parseInt(message.header(Message.CONTENT_LENGTH)));
            body.read(buffer);
            buffer.flip();
            write(buffer);
        } else {
            ByteBuffer buffer = ByteBuffer.allocate(32 * 1024);
            while (body.read(buffer) != -1 || buffer.position() > 0) {
                buffer.flip();
                write(buffer);
                buffer.compact();
            }
        }
    }
}
