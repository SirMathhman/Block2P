package com.meti.demos;

import com.meti.io.Peer;
import com.meti.io.Source;
import com.meti.io.Sources;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
import com.meti.io.connect.connections.Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
public class PeerDemo {

    private static ConnectionHandler fromHandler;

    //methods
    public static void main(String[] args) {
        init();
        loop();
    }

    private static void loop() {
        boolean shouldContinue;
        do {
            shouldContinue = fromHandler.isHandling();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (shouldContinue);

        System.exit(0);
    }

    private static void init() {
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
        fromHandler = new FromHandler();

        Peer from = new Peer(fromHandler);
        try {
            InetAddress address = InetAddress.getByName("localhost");
            int port = listener.getLocalPort();

            Socket socket = new Socket(address, port);
            Source socketSource = Sources.fromSocket(socket);
            Connection connection = new Connection(socketSource);
            from.initConnection(connection);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
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