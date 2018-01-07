package com.meti.demos;

import com.meti.io.Peer;
import com.meti.io.Source;
import com.meti.io.Sources;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
import com.meti.io.connect.connections.Connection;
import com.meti.util.EventHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

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
        //here we handle the peer that is being looked for
        //"the server"
        ConnectionHandler toHandler = new ToHandler();
        Peer to = new Peer(toHandler);
        ConnectionListener listener = null;
        try {
            listener = to.listen(0, Connection.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        //here we handle the peer that looks for the other peer
        //"the client"
        ConnectionHandler fromHandler = new FromHandler();
        Peer from = new Peer(fromHandler);
        try {
            InetAddress address = InetAddress.getByName("localhost");
            int port = listener.getLocalPort();

            Socket socket = new Socket(address, port);
            Source socketSource = Sources.fromSocket(socket);
            Connection connection = new Connection(socketSource);
            from.initConnection(connection);

            connection.getManager().put(Connection.PROPERTIES.ON_CLOSED, new EventHandler() {
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
            boolean shouldContinue;
            do {
                shouldContinue = !obj.getSource().isClosed();
            } while (shouldContinue);
            return true;
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