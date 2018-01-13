package com.meti.io.connect.connections;

import com.meti.io.Source;
import com.meti.util.event.EventManager;
import com.meti.util.event.Managable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Connection can read and write bytes from a Source.
 *
 * @author SirMathhman
 * @version 0.0.0
 * @see Source
 * @since 1/2/2018
 */
public class Connection implements Closeable, Managable {
    private final EventManager manager = new EventManager();
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

        manager.handle(EVENTS.ON_CLOSED, this);
    }

    @Override
    public EventManager getManager() {
        return manager;
    }

    /**
     * Reads a single byte from the Source's InputStream. {@link InputStream#read()}
     *
     * @return The byte.
     * @throws IOException If an Exception occurred.
     */
    public int read() throws IOException {
        checkClosed();

        return source.getInputStream().read();
    }

    private void checkClosed() {
        if (isClosed()) {
            throw new IllegalStateException("Connection is closed.");
        }
    }

    /**
     * Returns if the connection is closed.
     *
     * @return The state.
     */
    public boolean isClosed() {
        return source.isClosed();
    }

    /**
     * Writes a single byte from the Source's OutputStream {@link OutputStream#write(int)}
     * Make sure you flush the data!
     *
     * @param b The byte.
     * @throws IOException If an Exception occurred.
     */
    public void write(int b) throws IOException {
        checkClosed();

        source.getOutputStream().write(b);
    }

    /**
     * Flushes the Source's OutputStream {@link OutputStream#flush()}
     *
     * @throws IOException If an Exception occurred.
     */
    public void flush() throws IOException {
        checkClosed();

        source.getOutputStream().flush();
    }

    /**
     * Returns the internal Source.
     *
     * @return The Source.
     */
    public Source getSource() {
        return source;
    }

    /**
     * Returns true if the connection finds data to be read by the program.
     *
     * @return If data was found.
     * @throws IOException If an exception was thrown.
     */
    public boolean hasData() throws IOException {
        return source.getInputStream().available() > 0;
    }

    //anonymous

    /**
     * Types of events that the Connection can handle.
     */
    public enum EVENTS {
        ON_CLOSED
    }
}