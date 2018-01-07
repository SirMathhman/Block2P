package com.meti.demos;

import com.meti.io.Peer;
import com.meti.io.Sources;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.connections.Connection;
import com.meti.util.event.EventHandler;

import java.io.IOException;

import static com.meti.io.connect.connections.Connection.PROPERTIES.ON_CLOSED;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
public class PeerDemo {
    public static void main(String[] args) {
        start();
    }

    //methods
    private static void start() {
        try {
            int port = startTo();
            startFrom(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void startFrom(int port) throws IOException {
        Connection connection = new Connection(Sources.fromSocket(
                "localhost",
                port
        ));
        Peer from = new Peer(new FromHandler());
        from.initConnection(connection);

        connection.getManager().put(ON_CLOSED, new EventHandler() {
            @Override
            public Void handleImpl(Object obj) {
                stop();
                return null;
            }
        });
    }

    private static int startTo() throws IOException {
        Peer to = new Peer(new ToHandler());
        return to.listen(0, Connection.class).getLocalPort();
    }

    private static void stop() {
        System.exit(0);
    }

    private static class FromHandler extends ConnectionHandler {
        @Override
        public Boolean handleImpl(Connection obj) {
            try {
                System.out.println("Connected!");
                obj.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private static class ToHandler extends ConnectionHandler {
        @Override
        public Boolean handleImpl(Connection obj) {
            try {
                System.out.println("Found a connection!");
                obj.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}