package com.iancaffey.http;

import java.io.IOException;

/**
 * ExchangeFactory
 *
 * @author Ian Caffey
 * @since 1.0
 */
public interface ExchangeFactory extends AutoCloseable {
    public Exchange create() throws IOException;

    public boolean isClosed() throws IOException;
}
