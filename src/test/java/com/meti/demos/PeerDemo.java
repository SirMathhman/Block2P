package com.meti.demos;

import com.meti.io.Peer;
import com.meti.io.Sources;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
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
    //methods
    public static void main(String[] args) {
        start();
    }

    private static void start() {
        try {
            //here we handle the peer that is being looked for
            //"the server"
            Peer to = new Peer(new ToHandler());
            ConnectionListener listener = to.listen(0, Connection.class);

            //here we handle the peer that looks for the other peer
            //"the client"
            Connection connection = new Connection(Sources.fromSocket(
                    "localhost",
                    listener.getLocalPort()
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
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
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