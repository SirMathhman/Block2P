package com.meti.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class Source {
    private InputStream inputStream;
    private OutputStream outputStream;

    public Source(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public Source() {
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
