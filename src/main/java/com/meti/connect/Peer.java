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
    private final Set<SimpleConnectionListener> listenerSet = new HashSet<>();
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

    public SimpleConnectionListener listen(int port) {
        SimpleConnectionListener listener = new SimpleConnectionListener(port, this);
        listener.listen();

        listenerSet.add(listener);

        return listener;
    }

    public Boolean initConnection(Connection connection) throws Exception {
        connectionSet.add(connection);
        return handler.handle(connection);
    }

    public void close() throws IOException {
        for (SimpleConnectionListener listener : listenerSet) {
            listener.close();
        }

        for (Connection connection : connectionSet) {
            connection.close();
        }
    }
}
