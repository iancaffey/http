package com.iancaffey.http.server;

import com.iancaffey.http.io.HttpWriter;
import com.iancaffey.http.io.RequestVisitor;

import java.io.BufferedReader;
import java.net.Socket;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

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
     * <p>
     * Reading the header information is done using blocking I/O (BufferedReader) as doing so using the {@code SocketChannel}
     * would be counter-intuitive as each byte would have to be read in sequence.
     * <p>
     * However, the respond method delegation to the {@code RequestVisitor} is provided an {@code HttpWriter} which uses
     * non-blocking I/O operations.
     * <p>
     * {@code StandardCharsets.UTF_8} is used as the character set for decoding the request data.
     *
     * @param socket  the socket for the incoming connection/request
     * @param visitor the visitor to process the incoming HTTP request
     */
    @Override
    public void accept(Socket socket, RequestVisitor visitor) throws Exception {
        ByteChannel channel = socket.getChannel();
        BufferedReader reader = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8.newDecoder(), -1));
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
        visitor.respond(new HttpWriter(channel));
    }

    /**
     * No close operations required.
     */
    @Override
    public void close() {

    }
}
