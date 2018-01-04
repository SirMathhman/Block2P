package com.meti.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class Source<I extends InputStream, O extends OutputStream> {
    private final I inputStream;
    private final O outputStream;

    public Source(I inputStream, O outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public I getInputStream() {
        return inputStream;
    }

    public O getOutputStream() {
        return outputStream;
    }

    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
