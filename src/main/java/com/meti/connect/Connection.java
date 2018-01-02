package com.meti.connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class Connection {
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public Connection(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public int read() throws IOException {
        return inputStream.read();
    }

    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    public void flush() throws IOException {
        outputStream.flush();
    }

    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}