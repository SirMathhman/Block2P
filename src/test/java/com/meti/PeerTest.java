package com.meti;

import com.meti.connect.Connection;
import com.meti.connect.Peer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
class PeerTest {
    private Peer peer;

    @BeforeEach
    void before() {
        peer = new Peer(obj -> {
            System.out.println("Found a connection");
            return true;
        });
    }

    @AfterEach
    void after() throws IOException {
        peer.close();
    }

    @Test
    void listen() throws IOException {
        SimpleConnectionListener listener = peer.listen(0);
        Assertions.assertNotNull(listener);
    }

    @Test
    void initConnection() throws Exception {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);
        Connection connection = new Connection(inputStream, outputStream);
        Assertions.assertTrue(peer.initConnection(connection));
    }
}