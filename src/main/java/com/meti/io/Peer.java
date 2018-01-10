package com.meti.io;

import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
import com.meti.io.connect.connections.Connection;
import com.meti.util.event.EventManager;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * A peer is an entity that can send and receive data between other peers.
 * It can directly connect to another peer, or it can create a listener for other Peers to connect to.
 *
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class Peer implements Closeable {
    private final Set<ConnectionListener> listenerSet = new HashSet<>();
    private final Set<Connection> connectionSet = new HashSet<>();
    private final EventManager manager = new EventManager();
    private final ConnectionHandler handler;
    private final ExecutorService service;
    /**
     * Creates a peer with a handler that immediately
     *
     * @param handler The handler.
     */
    public Peer(ConnectionHandler handler) {
        this(handler, null);
    }

    /**
     * Constructs a Peer with a handler and an ExecutorService.
     *
     * @param handler The handler.
     * @param service The ExecutorService.
     */
    public Peer(ConnectionHandler handler, ExecutorService service) {
        this.handler = handler;
        this.service = service;
    }

    /**
     * Closes the Peer, along with any listeners and connections constructed by the Peer.
     *
     * @throws IOException If an exception occurred during closing any of the listeners and or connections.
     */
    @Override
    public void close() throws IOException {
        for (ConnectionListener listener : listenerSet) {
            listener.close();
        }

        for (Connection connection : connectionSet) {
            connection.close();
        }

        manager.handle(PROPERTIES.ON_CLOSED, this);
    }

    /**
     * <p>
     * Listens on a port with a Class extending Connection, connectionClass, to instantiate.
     * All threads constructed in the method will use the ExecutorService specified in the constructor.
     * If no ExecutorService is found to be used, then a simple thread is started.
     * Creates a ConnectionListener bound to the port, which then listens.
     * Upon finding a connection, a new instance of connectionClass is attempted at being created.
     * Finally, the connection is sent back up into the ConnectionHandler field to be used in the application if specified.
     * </p>
     *
     * @param port            The port.
     * @param connectionClass The Class extending Connection.
     * @param <T>             The type of Connection connectionClass should extend.
     * @return The created ConnectionListener.
     * @throws IOException If an Exception during constructing a ConnectionListener.
     */
    public <T extends Connection> ConnectionListener listen(int port, Class<T> connectionClass) throws IOException {
        ConnectionListener connectionListener;
        if (service != null) {
            connectionListener = new ConnectionListener(port, this);
        } else {
            connectionListener = new ConnectionListener(port, this, service);
        }

        connectionListener.initConnectionClass(connectionClass);
        connectionListener.listen();

        listenerSet.add(connectionListener);

        manager.handle(PROPERTIES.ON_LISTEN, this, connectionListener);
        return connectionListener;
    }

    /**
     * <p>
     * Initializes a direct connection to another Peer to be picked up by a ConnectionListener.
     * Attempts to start handling the connection on a new Thread,
     * or submitted to an ExecutorService if the constructor parameter was not null.
     * </p>
     *
     * @param connection The connection.
     * @return A Future indicating the result of the connection. If it is true, the connection was established and handled successfully, otherwise false.
     */
    public Future<Object> initConnection(final Connection connection) {
        connectionSet.add(connection);

        Callable<Object> callable = () -> handler.handle(connection);
        Future<Object> future;
        if (service == null) {
            FutureTask<Object> task = new FutureTask<>(callable);
            new Thread(task).start();

            future = task;
        } else {
            future = service.submit(callable);
        }

        manager.handle(PROPERTIES.ON_INIT_CONNECTION, this, connection);
        return future;
    }

    public EventManager getManager() {
        return manager;
    }

    //anonymous
    public enum PROPERTIES {
        ON_LISTEN, ON_INIT_CONNECTION, ON_CLOSED
    }
}
