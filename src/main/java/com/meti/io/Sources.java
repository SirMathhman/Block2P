package com.meti.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static com.meti.io.Source.Closeable;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class Sources {
    private Sources() {
    }

    //methods
    public static Source fromSocket(String address, int port) throws IOException {
        return fromSocket(InetAddress.getByName(address), port);
    }

    public static Source fromSocket(InetAddress address, int port) throws IOException {
        return fromSocket(new Socket(address, port));
    }

    public static Source fromSocket(Socket socket) throws IOException {
        return new Source(socket.getInputStream(), socket.getOutputStream(), new Closeable() {
            @Override
            public void close() throws IOException {
                socket.close();
            }

            @Override
            public boolean isClosed() {
                return socket.isClosed();
            }
        });
    }
}
