package com.iancaffey.http;

import com.iancaffey.http.util.MalformedRequestException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpReader
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class HttpReader {
    /**
     * HTTP request type for "GET". Requests data from a specified resource.
     */
    public static final String GET = "GET";
    /**
     * HTTP request type for "POST". Submits data to be processed to a specified resource.
     */
    public static final String POST = "POST";
    /**
     * HTTP request type for "DELETE". Deletes the specified resource.
     */
    public static final String DELETE = "DELETE";
    private final BufferedReader reader;
    private ReadState state;
    private String requestType;
    private String uri;
    private String version;
    private Map<String, String[]> headers;
    private BufferedReader body;

    public HttpReader(InputStream in) {
        if (in == null)
            throw new IllegalArgumentException();
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.state = ReadState.BEGIN;
    }

    public synchronized String readRequestType() {
        if (requestType == null)
            readSignatureFully();
        return requestType;
    }

    public synchronized String readUri() {
        if (requestType == null)
            readSignatureFully();
        return uri;
    }

    public synchronized String readVersion() {
        if (requestType == null)
            readSignatureFully();
        return version;
    }

    public synchronized String[] readHeader(String key) {
        if (headers == null) {
            readSignatureFully();
            readHeadersFully();
        }
        return headers.get(key);
    }

    public synchronized Map<String, String[]> readHeaders() {
        if (headers == null) {
            readSignatureFully();
            readHeadersFully();
        }
        return headers;
    }

    public synchronized BufferedReader readBody() {
        if (state != ReadState.END) {
            readSignatureFully();
            readHeadersFully();
            readBodyFully();
        }
        return body;
    }

    private void readSignatureFully() {
        if (state != ReadState.BEGIN)
            return;
        try {
            String first = reader.readLine();
            int firstIndex = first.indexOf(' ');
            int secondIndex = first.indexOf(' ', firstIndex + 1);
            this.requestType = first.substring(0, firstIndex).trim();
            this.uri = first.substring(firstIndex + 1, secondIndex).trim();
            this.version = first.substring(secondIndex + 1).trim();
            state = ReadState.HEADERS;
        } catch (Exception e) {
            throw new MalformedRequestException("Unable to parse incoming HTTP request.", e);
        }
    }

    private void readHeadersFully() {
        if (state != ReadState.HEADERS)
            return;
        try {
            Map<String, String[]> headers = new HashMap<>();
            String header;
            while ((header = reader.readLine()) != null) {
                if (header.isEmpty())
                    break;
                int colon = header.indexOf(':');
                if (colon == -1)
                    throw new IllegalStateException("Unable to handle header: " + header);
                headers.put(header.substring(0, colon).trim(), header.substring(colon + 1).trim().split(","));
            }
            this.headers = headers;
            state = ReadState.BODY;
        } catch (Exception e) {
            throw new MalformedRequestException("Unable to parse incoming HTTP request.", e);
        }
    }

    private void readBodyFully() {
        if (state != ReadState.BODY)
            return;
        this.body = reader;
        this.state = ReadState.END;
    }

    private static enum ReadState {
        BEGIN, HEADERS, BODY, END
    }
}
