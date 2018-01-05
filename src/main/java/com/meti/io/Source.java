package com.meti.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Source is a utility class to keep code clean.
 * A Source contains an InputStream and OutputStream in one class.
 *
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class Source {
    private final InputStream inputStream;
    private final OutputStream outputStream;

    /**
     * Constructs a Source from an InputStream and an OutputStream
     *
     * @param inputStream  The InputStream.
     * @param outputStream The OutputStream.
     */
    public Source(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
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

    /**
     * Closes the internal InputStream and OutputStream.
     *
     * @throws IOException If an Exception occurred.
     */
    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
