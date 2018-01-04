package com.meti.connect;

import com.meti.io.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;

class BufferedConnectionTest {

    @Test
    void read() throws Exception {
        ConnectionHandler handler = obj -> {
            Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
                int n1 = obj.read();
                int n2 = obj.read();
                int n3 = obj.read();

                Assertions.assertArrayEquals(new int[]{3, 4, 5}, new int[]{n1, n2, n3});
            });

            return true;
        };
        Peer peer = new Peer(handler);

        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);
        Source<InputStream, OutputStream> source = new Source<>(inputStream, outputStream);
        BufferedConnection connection = new BufferedConnection(source);
        peer.initConnection(connection);

        connection.write(3);
        connection.write(4);
        connection.write(5);
        connection.flush();

        peer.close();
    }
}