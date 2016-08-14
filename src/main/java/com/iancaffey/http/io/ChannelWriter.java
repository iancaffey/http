package com.iancaffey.http.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * ChannelWriter
 * <p>
 * An object providing a convenient interface for reading from and writing to {@code Channel}.
 * <p>
 * Primitive overloaded write methods are provided to avoid having to convert them all to string, encode them, and write them out.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class ChannelWriter implements AutoCloseable {
    private final ReadableByteChannel in;
    private final WritableByteChannel out;

    /**
     * Constructs a new {@code ChannelWriter} with specified channel to use as both input and output channels.
     *
     * @param channel the channel
     */
    public ChannelWriter(ByteChannel channel) {
        this(channel, channel);
    }

    /**
     * Constructs a new {@code ChannelWriter} with specified input and output channels.
     *
     * @param in  the input channel
     * @param out the output channel
     */
    public ChannelWriter(ReadableByteChannel in, WritableByteChannel out) {
        this.in = in;
        this.out = out;
    }

    /**
     * Returns the input channel.
     *
     * @return the input channel
     */
    public ReadableByteChannel in() {
        return in;
    }

    /**
     * Returns the output channel.
     *
     * @return the output channel
     */
    public WritableByteChannel out() {
        return out;
    }

    /**
     * Reads a sequence of bytes from the input channel to the buffer.
     * <p>
     * An attempt is made to read up to <i>r</i> bytes from the channel,
     * where <i>r</i> is the number of bytes remaining in the buffer, that is,
     * {@code buffer.remaining()}, at the moment this method is invoked.
     *
     * @param buffer the buffer to read into
     * @return the number of bytes read
     * @throws IOException indicating an error occurred while reading from the input channel
     */
    public int read(ByteBuffer buffer) throws IOException {
        return in == null ? -1 : in.read(buffer);
    }

    /**
     * Writes a sequence of bytes from the buffer to the output channel.
     * <p>
     * An attempt is made to write up to <i>r</i> bytes to the channel,
     * where <i>r</i> is the number of bytes remaining in the buffer, that is,
     * {@code buffer.remaining()}, at the moment this method is invoked.
     *
     * @param buffer the buffer containing the data to write
     * @return the number of bytes written
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public int write(ByteBuffer buffer) throws IOException {
        return out == null ? -1 : out.write(buffer);
    }

    /**
     * Writes the value out to the output channel.
     *
     * @param value the value to write
     * @return {@code true} if the value was successfully written out to the output channel
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public boolean write(byte value) throws IOException {
        return write(ByteBuffer.allocate(Byte.BYTES).put(value)) == Byte.BYTES;
    }

    /**
     * Writes the value out to the output channel.
     *
     * @param value the value to write
     * @return {@code true} if the value was successfully written out to the output channel
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public boolean write(byte[] value) throws IOException {
        return write(ByteBuffer.wrap(value)) == value.length;
    }

    /**
     * Writes the value out to the output channel.
     *
     * @param value the value to write
     * @return {@code true} if the value was successfully written out to the output channel
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public boolean write(char value) throws IOException {
        return write(ByteBuffer.allocate(Character.BYTES).putChar(value)) == Character.BYTES;
    }

    /**
     * Writes the value out to the output channel.
     *
     * @param value the value to write
     * @return {@code true} if the value was successfully written out to the output channel
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public boolean write(short value) throws IOException {
        return write(ByteBuffer.allocate(Short.BYTES).putShort(value)) == Short.BYTES;
    }

    /**
     * Writes the value out to the output channel.
     *
     * @param value the value to write
     * @return {@code true} if the value was successfully written out to the output channel
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public boolean write(int value) throws IOException {
        return write(ByteBuffer.allocate(Integer.BYTES).putInt(value)) == Integer.BYTES;
    }

    /**
     * Writes the value out to the output channel.
     *
     * @param value the value to write
     * @return {@code true} if the value was successfully written out to the output channel
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public boolean write(long value) throws IOException {
        return write(ByteBuffer.allocate(Long.BYTES).putLong(value)) == Long.BYTES;
    }

    /**
     * Writes the value out to the output channel.
     *
     * @param value the value to write
     * @return {@code true} if the value was successfully written out to the output channel
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public boolean write(double value) throws IOException {
        return write(ByteBuffer.allocate(Double.BYTES).putDouble(value)) == Double.BYTES;
    }

    /**
     * Writes out the encoded string to the output channel using the {@code StandardCharsets.UTF_8} character set.
     *
     * @param s the string to write
     * @return {@code true} if all encoded bytes were written out to the output channel
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public boolean write(String s) throws IOException {
        return write(s, StandardCharsets.UTF_8);
    }

    /**
     * Writes out the encoded string to the output channel.
     *
     * @param s       the string to write
     * @param charset the charset used for encoding the string
     * @return {@code true} if all encoded bytes were written out to the output channel
     * @throws IOException indicating an error occurred while writing out to the output channel
     */
    public boolean write(String s, Charset charset) throws IOException {
        return write(s.getBytes(charset));
    }

    /**
     * Closes both the the input and output channels.
     *
     * @throws Exception indicating an I/O exception occurred during the close operations
     */
    @Override
    public void close() throws Exception {
        if (in != null)
            in.close();
        if (out != null)
            out.close();
    }
}
