package com.meti.connect;

import com.meti.Peer;
import com.meti.connect.connections.Connection;
import com.meti.io.Source;
import com.meti.util.Loop;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
class ConnectionTest {
    private static final PipedInputStream inputStream = new PipedInputStream();
    private static final PipedOutputStream outputStream = new PipedOutputStream();

    private static Peer peer;
    private static Connection connection;

    //methods
    @BeforeAll
    static void construct() throws IOException {
        ConnectionHandler handler = obj -> true;
        peer = new Peer(handler);

        inputStream.connect(outputStream);

        Source source = new Source(inputStream, outputStream);
        connection = new Connection(source);

        new Thread(new ConnectionTestRunnable()).start();
    }

    @AfterAll
    static void deconstruct() throws IOException {
        peer.close();
    }

    @Test
    void stream() throws IOException {
        connection.write(100);
        connection.flush();
        Assertions.assertEquals(100, connection.read());

        connection.close();
    }

    private static class ConnectionTestRunnable extends Loop {
        @Override
        protected void loop() throws Exception {
            int b = inputStream.read();
            outputStream.write(b);
            outputStream.flush();
        }
    }
}