package com.meti.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Source is a utility class to keep code clean.
 * A Source contains an InputStream and OutputStream in one class.
 * Sources can also be created from {@link Sources}
 *
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class Source implements Closeable {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Closeable parent;

    private boolean closed = false;

    /**
     * Constructs a Source from an InputStream and an OutputStream
     *
     * @param inputStream  The InputStream.
     * @param outputStream The OutputStream.
     */
    public Source(InputStream inputStream, OutputStream outputStream) {
        this(inputStream, outputStream, null);
    }

    public Source(InputStream inputStream, OutputStream outputStream, Closeable parent) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.parent = parent;
    }

    /**
     * Closes the internal InputStream and OutputStream.
     *
     * @throws IOException If an Exception occurred.
     */
    @Override
    public void close() throws IOException {
        closed = true;

        inputStream.close();
        outputStream.close();

        if (parent != null) {
            parent.close();
        }
    }

    /**
     * Gets the InputStream.
     *
     * @return The InputStream.
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Gets the OutputStream.
     *
     * @return The OutputStream.
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    public boolean isClosed() {
        return closed;
    }
}
