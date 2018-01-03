package com.meti.connect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;

class BufferedConnectionTest {

    @Test
    void read() throws Exception {
        ConnectionHandler handler = obj -> {
            obj.write(obj.read());
            obj.flush();
            return true;
        };
        Peer peer = new Peer(handler);

        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);
        BufferedConnection connection = new BufferedConnection(inputStream, outputStream);
        peer.initConnection(connection);

        connection.write(3);
        connection.write(4);
        connection.write(5);
        connection.flush();

        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            int n1 = connection.read();
            int n2 = connection.read();
            int n3 = connection.read();

            Assertions.assertArrayEquals(new int[]{3, 4, 5}, new int[]{n1, n2, n3});

            peer.close();
        });
    }
}