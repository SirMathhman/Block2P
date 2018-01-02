package com.meti.connect;

import com.meti.util.Loop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class ConnectionListener {
    private final ServerSocket serverSocket;
    private final Peer parent;

    private boolean listening = false;

    public ConnectionListener(int port, Peer parent) throws IOException {
        serverSocket = new ServerSocket(port);
        this.parent = parent;
    }

    public void listen() {
        listening = true;

        //TODO: provide the ability to start from an ExecutorService
        new Thread(new ConnectionListenerLoop()).start();
    }

    public void close() throws IOException {
        if (!listening) throw new IllegalStateException("Listener is not listening, cannot be closed yet!");

        serverSocket.close();
    }

    private class ConnectionListenerLoop extends Loop {
        @Override
        protected void loop() throws Exception {
            try {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket.getInputStream(), socket.getOutputStream());
                parent.initConnection(connection);
            } catch (SocketException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
