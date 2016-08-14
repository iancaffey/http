package com.iancaffey.http.server;

import com.iancaffey.http.io.RequestVisitor;
import com.iancaffey.http.io.ResponseWriter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * StandardSocketHandler
 * <p>
 * An object provided with direct access to the underlying socket for the incoming HTTP request.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class StandardSocketHandler implements SocketHandler {
    /**
     * Accepts a {@code RequestVisitor} to be used on the incoming HTTP request.
     *
     * @param socket  the socket for the incoming connection/request
     * @param visitor the visitor to process the incoming HTTP request
     */
    @Override
    public void accept(Socket socket, RequestVisitor visitor) throws Exception {
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String first = reader.readLine();
        int firstIndex = first.indexOf(' ');
        int secondIndex = first.indexOf(' ', firstIndex + 1);
        visitor.visitRequest(first.substring(0, firstIndex).trim(),
                first.substring(firstIndex + 1, secondIndex).trim(),
                first.substring(secondIndex + 1).trim());
        String header;
        while ((header = reader.readLine()) != null) {
            if (header.isEmpty())
                break;
            int colon = header.indexOf(':');
            if (colon == -1)
                throw new IllegalStateException("Unable to handle header: " + header);
            visitor.visitHeader(header.substring(0, colon).trim(), header.substring(colon + 1).trim());
        }
        visitor.respond(new ResponseWriter(in, out));
    }

    /**
     * No close operations required.
     */
    @Override
    public void close() {

    }
}
