package com.meti.connect;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class Peer {
    private final Set<ConnectionListener> listenerSet = new HashSet<>();
    private final Set<Connection> connectionSet = new HashSet<>();

    private final ConnectionHandler handler;
    private final ExecutorService service;

    public Peer() {
        this(obj -> {
            obj.close();
            return true;
        }, null);
    }

    public Peer(ExecutorService service) {
        this(obj -> {
            obj.close();
            return true;
        }, service);
    }

    public Peer(ConnectionHandler handler) {
        this(handler, null);
    }

    public Peer(ConnectionHandler handler, ExecutorService service) {
        this.handler = handler;
        this.service = service;
    }

    public <T extends Connection> ConnectionListener listen(int port, Class<T> c) throws IOException {
        ConnectionListener connectionListener = new ConnectionListener(port, this);
        connectionListener.initConnectionClass(c);
        connectionListener.listen();

        listenerSet.add(connectionListener);

        return connectionListener;
    }

    public Boolean initConnection(final Connection connection) throws Exception {
        connectionSet.add(connection);

        Callable<Boolean> callable = () -> handler.handle(connection);
        Future<Boolean> future;
        if (service == null) {
            FutureTask<Boolean> task = new FutureTask<>(callable);
            new Thread(task).start();

            future = task;
        } else {
            future = service.submit(callable);
        }
        return future.get();
    }

    public void close() throws IOException {
        for (ConnectionListener listener : listenerSet) {
            listener.close();
        }

        for (Connection connection : connectionSet) {
            connection.close();
        }
    }
}
