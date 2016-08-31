package com.iancaffey.http;

import java.io.IOException;

/**
 * ExchangeHandler
 *
 * @author Ian Caffey
 * @since 1.0
 */
public interface ExchangeHandler {
    public void accept(Exchange exchange) throws IOException;
}
