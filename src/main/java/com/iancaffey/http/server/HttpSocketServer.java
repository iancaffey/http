package com.iancaffey.http.server;

import com.iancaffey.http.HttpServer;
import com.iancaffey.http.io.RequestVisitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

/**
 * HttpSocketServer
 * <p>
 * An object representing a basic HTTP server backup by a {@code SocketServer}, supporting the full HTTP 1.1 specification.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class HttpSocketServer implements HttpServer {
    private final ServerSocket server;
    private final SocketHandler handler;

    /**
     * Constructs a new {@code HttpSocketServer} bound to a specified port.
     * <p>
     * Using {@code new AsynchronousSocketHandler(Executors.newFixedThreadPool(20), new StandardSocketHandler())} as the socket handler.
     *
     * @param port the port to bind
     * @throws IOException indicating an I/O error occurs when opening the server
     */
    public HttpSocketServer(int port) throws IOException {
        this(port, new AsynchronousSocketHandler(Executors.newFixedThreadPool(20), new StandardSocketHandler()));
    }

    /**
     * Constructs a new {@code HttpSocketServer} bound to a specified port with a request pool size limit.
     * <p>
     * The request pool size limit restricts enqueued requests by refusing the requests when the queue reaches the limit.
     * <p>
     * Using {@code new AsynchronousSocketHandler(Executors.newFixedThreadPool(20), new StandardSocketHandler())} as the socket handler.
     *
     * @param port     the port to bind
     * @param poolSize the maximum enqueue request pool size
     * @throws IOException indicating an I/O error occurs when opening the server
     */
    public HttpSocketServer(int port, int poolSize) throws IOException {
        this(port, poolSize, new AsynchronousSocketHandler(Executors.newFixedThreadPool(20), new StandardSocketHandler()));
    }

    /**
     * Constructs a new {@code HttpSocketServer} bound to a specified port.
     *
     * @param port    the port to bind
     * @param handler the socket handler
     * @throws IOException indicating an I/O error occurs when opening the server
     */
    public HttpSocketServer(int port, SocketHandler handler) throws IOException {
        if (handler == null || port < 0 || port > 65535)
            throw new IllegalArgumentException();
        this.handler = handler;
        this.server = new ServerSocket(port);
    }

    /**
     * Constructs a new {@code HttpSocketServer} bound to a specified port with a request pool size limit.
     * <p>
     * The request pool size limit restricts enqueued requests by refusing the requests when the queue reaches the limit.
     *
     * @param port     the port to bind
     * @param poolSize the maximum enqueue request pool size
     * @param handler  the socket handler
     * @throws IOException indicating an I/O error occurs when opening the server
     */
    public HttpSocketServer(int port, int poolSize, SocketHandler handler) throws IOException {
        if (handler == null || port < 0 || port > 65535)
            throw new IllegalArgumentException();
        this.handler = handler;
        this.server = new ServerSocket(port, poolSize);

    }

    /**
     * Constructs a new {@code HttpSocketServer} bound to a specified port and local IP address with a request pool size limit.
     * <p>
     * The request pool size limit restricts enqueued requests by refusing the requests when the queue reaches the limit.
     *
     * @param port     the port to bind
     * @param poolSize the maximum enqueue request pool size
     * @param address  the local IP address to bind
     * @param handler  the socket handler
     * @throws IOException indicating an I/O error occurs when opening the server
     */
    public HttpSocketServer(int port, int poolSize, InetAddress address, SocketHandler handler) throws IOException {
        if (handler == null || port < 0 || port > 65535)
            throw new IllegalArgumentException();
        this.handler = handler;
        this.server = new ServerSocket(port, poolSize, address);
    }

    /**
     * Accepts a {@code RequestVisitor} to be used on the next incoming HTTP request.
     * <p>
     * The method will block until a new request (or a previously enqueued request) can be found.
     *
     * @param visitor the visitor to process the incoming HTTP request
     */
    @Override
    public void accept(RequestVisitor visitor) throws Exception {
        if (server.isClosed())
            throw new IllegalStateException("HttpServer closed.");
        handler.accept(server.accept(), visitor);
    }

    /**
     * Closes the underlying socket server, immediately shutting down currently handled requests.
     *
     * @throws Exception indicating an error occurred shutting down currently blocked sockets being handled by a {@code RequestVisitor}
     */
    public void close() throws Exception {
        handler.close();
        server.close();
    }
}
