package com.meti.io;

import java.io.IOException;
import java.net.Socket;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class Sources {
    private Sources() {
    }

    public static Source fromSocket(Socket socket) throws IOException {
        return new Source(socket.getInputStream(), socket.getOutputStream());
    }
}
