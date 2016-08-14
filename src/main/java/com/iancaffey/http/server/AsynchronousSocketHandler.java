package com.iancaffey.http.server;

import com.iancaffey.http.io.RequestVisitor;

import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * AsynchronousSocketHandler
 * <p>
 * An object provided with direct access to the underlying socket for the incoming HTTP request.
 * <p>
 * An {@code ExecutorService} is used for executing the delegate {@code SocketHandler}.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class AsynchronousSocketHandler implements SocketHandler {
    private final ExecutorService executor;
    private final SocketHandler handler;

    /**
     * Constructs a new {@code AsynchronousSocketHandler} with an executor service and delegate socket handler.
     *
     * @param executor the executor service
     * @param handler  the delegate socket handler
     */
    public AsynchronousSocketHandler(ExecutorService executor, SocketHandler handler) {
        if (executor == null || handler == null)
            throw new IllegalArgumentException();
        this.executor = executor;
        this.handler = handler;
    }

    /**
     * Accepts a {@code RequestVisitor} to be used on the incoming HTTP request.
     * <p>
     * A new {@code Callable<Void>} is created and submitted to the {@code ExecutorService} to execute the socket handler.
     *
     * @param socket  the socket for the incoming connection/request
     * @param visitor the visitor to process the incoming HTTP request
     */
    @Override
    public void accept(Socket socket, RequestVisitor visitor) throws Exception {
        executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                handler.accept(socket, visitor);
                return null;
            }
        });
    }

    /**
     * Shuts down the {@code ExecutorService}.
     */
    @Override
    public void close() {
        executor.shutdown();
    }
}
