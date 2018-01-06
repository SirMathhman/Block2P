package com.meti.io.connect.connections;

import com.meti.io.Source;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Connection can read and write bytes from a Source.
 * @see Source
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class Connection implements Closeable {
    private final Source source;

    /**
     * Constructs a Connection from a specified Source.
     *
     * @param source The Source.
     */
    public Connection(Source source) {
        this.source = source;
    }

    /**
     * Closes the connection via {@link Source#close()}
     *
     * @throws IOException If an Exception occurred.
     */
    @Override
    public void close() throws IOException {
        source.close();
    }

    /**
     * Reads a single byte from the Source's InputStream. {@link InputStream#read()}
     *
     * @return The byte.
     * @throws IOException If an Exception occurred.
     */
    public int read() throws IOException {
        return source.getInputStream().read();
    }

    /**
     * Writes a single byte from the Source's OutputStream {@link OutputStream#write(int)}
     * Make sure you flush the data!
     *
     * @param b The byte.
     * @throws IOException If an Exception occurred.
     */
    public void write(int b) throws IOException {
        source.getOutputStream().write(b);
    }

    /**
     * Flushes the Source's OutputStream {@link OutputStream#flush()}
     * @throws IOException If an Exception occurred.
     */
    public void flush() throws IOException {
        source.getOutputStream().flush();
    }

    /**
     * Returns the internal Source.
     * @return The Source.
     */
    public Source getSource() {
        return source;
    }
}