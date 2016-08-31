package com.iancaffey.http;

import java.io.IOException;

/**
 * Server
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Server implements AutoCloseable {
    private final ExchangeFactory factory;
    private final ExchangeHandler handler;

    public Server(ExchangeFactory factory, ExchangeHandler handler) {
        if (factory == null || handler == null)
            throw new IllegalArgumentException();
        this.factory = factory;
        this.handler = handler;
    }

    public void await() throws IOException {
        handler.accept(factory.create());
    }

    public void listen() throws IOException {
        while (!isClosed())
            handler.accept(factory.create());
    }

    public boolean isClosed() throws IOException {
        return factory.isClosed();
    }

    @Override
    public void close() throws Exception {
        factory.close();
    }
}
