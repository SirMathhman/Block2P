package com.meti.connect;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class Peer {
    private final Set<ConnectionListener> listenerSet = new HashSet<>();
    private final Set<Connection> connectionSet = new HashSet<>();

    private final ConnectionHandler handler;

    public Peer() {
        this(obj -> {
            obj.close();
            return true;
        });
    }

    public Peer(ConnectionHandler handler) {
        this.handler = handler;
    }

    public <T extends Connection> ConnectionListener listen(int port, Class<T> c) throws IOException {
        ConnectionListener connectionListener = new ConnectionListener(port, this);
        connectionListener.initConnectionClass(c);
        connectionListener.listen();

        listenerSet.add(connectionListener);

        return connectionListener;
    }

    public Boolean initConnection(Connection connection) throws Exception {
        connectionSet.add(connection);
        return handler.handle(connection);
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
