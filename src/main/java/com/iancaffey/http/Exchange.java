package com.iancaffey.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Exchange
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Exchange implements AutoCloseable {
    public final InputStream in;
    public final OutputStream out;

    public Exchange(InputStream in, OutputStream out) {
        if (in == null || out == null)
            throw new IllegalArgumentException();
        this.in = in;
        this.out = out;
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
    }
}
