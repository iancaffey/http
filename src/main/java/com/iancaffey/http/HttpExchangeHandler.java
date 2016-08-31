package com.iancaffey.http;

import com.iancaffey.http.routes.Controller;
import com.iancaffey.http.util.MalformedRequestException;
import com.iancaffey.http.util.ResponseCode;
import com.iancaffey.http.util.RoutingException;

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
            Controller.code(ResponseCode.BAD_REQUEST).accept(reader, writer);
        } catch (RoutingException e) {
            Controller.code(ResponseCode.NOT_FOUND).accept(reader, writer);
        } catch (Exception e) {
            e.printStackTrace();
            Controller.code(ResponseCode.INTERNAL_SERVER_ERROR).accept(reader, writer);
        }
    }
}
