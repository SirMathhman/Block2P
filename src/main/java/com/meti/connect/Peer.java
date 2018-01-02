package com.meti.connect;

import com.meti.util.Handler;

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

    private final Handler<Connection, Boolean> connectionHandler;

    public Peer() {
        this(obj -> {
            obj.close();
            return true;
        });
    }

    public Peer(Handler<Connection, Boolean> connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public ConnectionListener listen(int port) throws IOException {
        ConnectionListener listener = new ConnectionListener(port, this);
        listener.listen();

        listenerSet.add(listener);

        return listener;
    }

    public Boolean initConnection(Connection connection) throws Exception {
        connectionSet.add(connection);
        return connectionHandler.handle(connection);
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
