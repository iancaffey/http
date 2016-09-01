package com.iancaffey.http;

import com.iancaffey.http.util.MalformedRequestException;
import com.iancaffey.http.util.RoutingException;
import com.iancaffey.http.util.Status;

import java.io.IOException;

/**
 * HttpExchangeHandler
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class HttpExchangeHandler implements ExchangeHandler {
    private final HttpHandler handler;

    public HttpExchangeHandler(HttpHandler handler) {
        if (handler == null)
            throw new IllegalArgumentException();
        this.handler = handler;
    }

    @Override
    public void accept(Exchange exchange) throws IOException {
        HttpReader reader = new HttpReader(exchange.in);
        HttpWriter writer = new HttpWriter(exchange.out);
        try {
            handler.accept(reader, writer);
        } catch (MalformedRequestException e) {
            Status.badRequest().accept(reader, writer);
        } catch (RoutingException e) {
            Status.notFound().accept(reader, writer);
        } catch (Exception e) {
            e.printStackTrace();
            Status.internalServerError().accept(reader, writer);
        }
    }
}
