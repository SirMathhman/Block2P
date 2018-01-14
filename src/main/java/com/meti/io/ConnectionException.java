package com.meti.io;

import com.meti.io.connect.connections.Connection;

/**
 * <p>
 * Indicates that there was a problem when using a Connection.
 *
 * @author SirMathhman
 * @version 0.0.0
 * @see Connection
 * </p>
 * @since 1/12/2018
 */
public class ConnectionException extends Exception {
    /**
     * Constructs a new ConnectionException with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public ConnectionException() {
    }

    /**
     * Constructs a new ConnectionException with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ConnectionException(String message) {
        super(message);
    }
}
