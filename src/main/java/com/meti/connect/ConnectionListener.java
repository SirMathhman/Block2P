package com.meti.connect;

import com.meti.Peer;
import com.meti.connect.connections.Connection;
import com.meti.connect.connections.SimpleConnection;
import com.meti.io.Source;
import com.meti.io.Sources;
import com.meti.util.Loop;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class ConnectionListener {
    private final ExecutorService service;
    private final ServerSocket serverSocket;
    private final Peer parent;

    private Class<?> connectionClass;

    private boolean listening = false;
    private Constructor<?> toUse;

    public ConnectionListener(int port, Peer parent) throws IOException {
        this(port, parent, null);
    }

    public ConnectionListener(int port, Peer parent, ExecutorService service) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.parent = parent;
        this.service = service;

        initConnectionClass(SimpleConnection.class);
    }

    public void initConnectionClass(Class<?> connectionClass) {
        Constructor<?>[] constructors = connectionClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            Parameter[] parameters = constructor.getParameters();

            if (Source.class.isAssignableFrom(parameters[0].getType())) {
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

        Loop loop = new ConnectionListenerLoop();
        if (service == null) {
            new Thread(loop).start();
        } else {
            service.submit(loop);
        }
    }

    public void close() throws IOException {
        if (!listening) throw new IllegalStateException("Listener is not listening, cannot be closed yet!");

        serverSocket.close();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    private class ConnectionListenerLoop extends Loop {
        @Override
        protected void loop() throws Exception {
            try {
                Socket socket = serverSocket.accept();
                Object obj = toUse.newInstance(Sources.fromSocket(socket));
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
