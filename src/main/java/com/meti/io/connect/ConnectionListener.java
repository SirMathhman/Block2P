package com.meti.io.connect;

import com.meti.io.Peer;
import com.meti.io.Source;
import com.meti.io.Sources;
import com.meti.io.connect.connections.Connection;
import com.meti.util.EventManager;
import com.meti.util.Loop;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * A ConnectionListener is created by a Peer to search for incoming Connections.
 * If an ExecutorService is specified in the constructor, then it is used to create threads.
 * Make sure {@link #listen()} is declared for the ConnectionListener to function.
 * </p>
 *
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class ConnectionListener implements Closeable {
    private final EventManager manager = new EventManager();
    private final ExecutorService service;
    private final ServerSocket serverSocket;
    private final Peer parent;
    private Class<?> connectionClass;
    private Constructor<?> toUse;

    /**
     * <p>
     * Constructs a ConnectionListener from a port and a parent Peer.
     * Calls {@link #ConnectionListener(int, Peer, ExecutorService)} with a null ExecutorService.
     * </p>
     *
     * @param port   The port.
     * @param parent The parent.
     * @throws IOException If an Exception occurred when constructing this object.
     */
    public ConnectionListener(int port, Peer parent) throws IOException {
        this(port, parent, null);
    }

    /**
     * Constructs a ConnectionListener from a port, parent Peer, and an ExecutorService.
     * The parent is required because that is the least complex way of invoking the ConnectionHandler
     * inside of parent.
     *
     * @param port    The port.
     * @param parent  The peer.
     * @param service The service.
     * @throws IOException If an Exception occured when hosting on the port.
     */
    public ConnectionListener(int port, Peer parent, ExecutorService service) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.parent = parent;
        this.service = service;
    }

    /**
     * Closes the ConnectionListener.
     *
     * @throws IOException If an Exception occurred when closing.
     */
    @Override
    public void close() throws IOException {
        serverSocket.close();

        manager.handle(PROPERTIES.ON_CLOSE, this);
    }

    /**
     * Sets and initializes the subclass of Connection to be instantiated of.
     * Searches for a constructor which only takes in a Source object.
     * {@link Connection#Connection(Source)}
     *
     * @param connectionClass The subclass.
     * @throws IllegalStateException If no constructors with Source could be found.
     * @see Source
     * @see Connection
     */
    public void initConnectionClass(Class<?> connectionClass) {
        Constructor<?>[] constructors = connectionClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            Parameter[] parameters = constructor.getParameters();

            if (Source.class.isAssignableFrom(parameters[0].getType())) {
                toUse = constructor;
            }
        }

        if (toUse == null) {
            throw new IllegalStateException("No valid constructor found for connections in " + this.connectionClass.getName() + "!");
        } else {
            this.connectionClass = connectionClass;
        }
    }

    /**
     * Listeners for peers on the specified port.
     * Creates a new Thread for listening, or on the ExecutorService if specified.
     */
    public void listen() {
        Loop loop = new ConnectionListenerLoop();
        if (service == null) {
            new Thread(loop).start();
        } else {
            service.submit(loop);
        }
    }

    /**
     * Returns the internal ServerSocket used by the ConnectionListener.
     *
     * @return The ServerSocket.
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Gets the local port established for this listener.
     *
     * @return The local port.
     */
    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    //anonymous
    public enum PROPERTIES {
        ON_SOCKET_ACCEPTED, ON_CLOSE

    }

    private class ConnectionListenerLoop extends Loop {
        @Override
        protected void loop() throws Exception {
            try {
                Socket socket = serverSocket.accept();
                manager.handle(PROPERTIES.ON_SOCKET_ACCEPTED, this, socket);

                Object obj = toUse.newInstance(Sources.fromSocket(socket));
                if (obj instanceof Connection) {
                    Connection connection = (Connection) obj;
                    parent.initConnection(connection);
                } else {
                    throw new IllegalStateException("Constructor in " + connectionClass.getName() + " does not return a connection!");
                }
            } catch (SocketException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
