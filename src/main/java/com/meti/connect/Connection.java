package com.meti.connect;

import com.meti.io.Source;

import java.io.IOException;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class Connection<T extends Source> {
    private final T source;

    public Connection(T source) {
        this.source = source;
    }

    public int read() throws IOException {
        return source.getInputStream().read();
    }

    public void write(int b) throws IOException {
        source.getOutputStream().write(b);
    }

    public void flush() throws IOException {
        source.getOutputStream().flush();
    }

    public void close() throws IOException {
        source.close();
    }

    public T getSource() {
        return source;
    }
}