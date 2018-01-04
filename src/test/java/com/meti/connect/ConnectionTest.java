package com.meti.connect;

import com.meti.io.Source;
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
    private static Peer peer;
    private static Connection connection;
    private static PipedInputStream inputStream = new PipedInputStream();
    private static PipedOutputStream outputStream = new PipedOutputStream();

    @BeforeAll
    static void construct() throws IOException {
        peer = new Peer();

        inputStream.connect(outputStream);

        Source source = new Source(inputStream, outputStream);
        connection = new Connection(source);

        new Thread(new ConnectionTestRunnable());
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

    private static class ConnectionTestRunnable implements Runnable {
        @Override
        public void run() {
            try {
                int b = inputStream.read();
                outputStream.write(b);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}