package com.iancaffey.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SocketExchangeFactory
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class SocketExchangeFactory implements ExchangeFactory {
    private final ServerSocket server;

    public SocketExchangeFactory(ServerSocket server) {
        if (server == null)
            throw new IllegalArgumentException();
        this.server = server;
    }

    @Override
    public Exchange create() throws IOException {
        Socket socket = server.accept();
        return new Exchange(socket.getInputStream(), socket.getOutputStream()) {
            @Override
            public void close() throws IOException {
                super.close();
                socket.close();
            }
        };
    }

    @Override
    public boolean isClosed() throws IOException {
        return server.isClosed();
    }

    @Override
    public void close() throws Exception {
        server.close();
    }
}
