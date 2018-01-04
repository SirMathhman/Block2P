package com.meti.connect;

import com.meti.io.Source;

import java.io.IOException;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class Connection {
    private final Source source;

    public Connection(Source source) {
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

    public Source getSource() {
        return source;
    }
}