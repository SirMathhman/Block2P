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
public class ConnectionListener<T extends Connection> {
    private final ServerSocket serverSocket;
    private final Peer parent;
    private final Class<T> connectionClass;

    private boolean listening = false;

    public ConnectionListener(int port, Peer parent, Class<T> connectionClass) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.parent = parent;
        this.connectionClass = connectionClass;
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

            /*    Constructor<?> toUse = null;
                Constructor<?>[] constructors = connectionClass.getConstructors();
                for (int i = 0; i < constructors.length; i++) {
                    Constructor<?> constructor = constructors[i];
                    Parameter[] parameters = constructor.getParameters();
                    if (InputStream.class.isAssignableFrom(parameters[0].getType()) &&
                            OutputStream.class.isAssignableFrom(parameters[1].getType())) {
                        toUse = constructor;
                    }
                }

                if(toUse == null){
                    throw new IllegalSTAe
                            ''
                }
                Object obj = toUse.newInstance(socket.getInetAddress(), socket.getOutputStream());
*/

                parent.initConnection(connection);
            } catch (SocketException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
