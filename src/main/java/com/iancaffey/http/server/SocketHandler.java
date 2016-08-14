package com.iancaffey.http.server;

import com.iancaffey.http.io.RequestVisitor;

import java.net.Socket;

/**
 * SocketHandler
 * <p>
 * An object provided with direct access to the underlying socket for the incoming HTTP request.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public interface SocketHandler extends AutoCloseable {
    /**
     * Accepts a {@code RequestVisitor} to be used on the incoming HTTP request.
     *
     * @param socket  the socket for the incoming connection/request
     * @param visitor the visitor to process the incoming HTTP request
     */
    public void accept(Socket socket, RequestVisitor visitor) throws Exception;
}
