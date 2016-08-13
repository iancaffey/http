package com.iancaffey.http;

import com.iancaffey.http.io.RequestVisitor;
import com.iancaffey.http.io.ResponseWriter;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * HttpServer
 * <p>
 * An object representing a basic HTTP server, supporting the full HTTP 1.1 specification.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class HttpServer implements AutoCloseable {
    private final ServerSocket socket;

    /**
     * Constructs a new {@code HttpServer} bound to a specified port.
     *
     * @param port the port to bind
     * @throws IOException indicating an I/O error occurs when opening the socket
     */
    public HttpServer(int port) throws IOException {
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException();
        this.socket = new ServerSocket(port);
    }

    /**
     * Constructs a new {@code HttpServer} bound to a specified port with a request pool size limit.
     * <p>
     * The request pool size limit restricts enqueued requests by refusing the requests when the queue reaches the limit.
     *
     * @param port     the port to bind
     * @param poolSize the maximum enqueue request pool size
     * @throws IOException indicating an I/O error occurs when opening the socket
     */
    public HttpServer(int port, int poolSize) throws IOException {
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException();
        this.socket = new ServerSocket(port, poolSize);

    }

    /**
     * Constructs a new {@code HttpServer} bound to a specified port and local IP address with a request pool size limit.
     * <p>
     * The request pool size limit restricts enqueued requests by refusing the requests when the queue reaches the limit.
     *
     * @param port     the port to bind
     * @param poolSize the maximum enqueue request pool size
     * @param address  the local IP address to bind
     * @throws IOException indicating an I/O error occurs when opening the socket
     */
    public HttpServer(int port, int poolSize, InetAddress address) throws IOException {
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException();
        this.socket = new ServerSocket(port, poolSize, address);
    }

    /**
     * Accepts a {@code RequestVisitor} to be used on the next incoming HTTP request.
     * <p>
     * The method will block until a new request (or a previously enqueued request) can be found.
     *
     * @param visitor the visitor to process the incoming HTTP request
     * @throws Exception indicating an error occurred reading the request or generating the response
     */
    public void accept(RequestVisitor visitor) throws Exception {
        if (socket.isClosed())
            throw new IOException("HttpServer closed.");
        final Socket socket = HttpServer.this.socket.accept();
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String first = reader.readLine();
        int firstIndex = first.indexOf(' ');
        int secondIndex = first.indexOf(' ', firstIndex + 1);
        visitor.visitRequest(first.substring(0, firstIndex).trim(),
                first.substring(firstIndex + 1, secondIndex).trim(),
                first.substring(secondIndex + 1).trim());
        String header;
        while ((header = reader.readLine()) != null) {
            if (header.isEmpty())
                break;
            int colon = header.indexOf(':');
            if (colon == -1)
                throw new IllegalStateException("Unable to handle header: " + header);
            visitor.visitHeader(header.substring(0, colon).trim(), header.substring(colon + 1).trim());
        }
        visitor.respond(new ResponseWriter(in, out));
    }

    /**
     * Closes the underlying socket, immediately shutting down currently handled requests.
     *
     * @throws Exception indicating an error occurred shutting down currently blocked sockets being handled by a {@code RequestVisitor}
     */
    public void close() throws Exception {
        socket.close();
    }
}
