package com.meti.connect;

import com.meti.util.Loop;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
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

    private Class<?> connectionClass;

    private boolean listening = false;
    private Constructor<?> toUse;

    public ConnectionListener(int port, Peer parent) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.parent = parent;
        initConnectionClass(SimpleConnection.class);
    }

    public void initConnectionClass(Class<?> connectionClass) {
        Constructor<?>[] constructors = connectionClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            Parameter[] parameters = constructor.getParameters();

            //TODO: change to sources
            if (InputStream.class.isAssignableFrom(parameters[0].getType()) &&
                    OutputStream.class.isAssignableFrom(parameters[1].getType())) {
                toUse = constructor;
            }
        }

        if (toUse == null) {
            throw new IllegalStateException("No valid constructor found for connections in " + this.connectionClass.getName() + "!");
        } else {
            this.connectionClass = connectionClass;
        }
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
                Object obj = toUse.newInstance(socket.getInetAddress(), socket.getOutputStream());
                if (obj instanceof Connection) {
                    Connection connection = (Connection) obj;
                    parent.initConnection(connection);
                } else {
                    throw new IllegalStateException("Constructor in " + connectionClass.getName() + " does not return a connection!");
                }
            } catch (SocketException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
