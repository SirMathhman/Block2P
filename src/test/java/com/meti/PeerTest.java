package com.meti;

import com.meti.io.Peer;
import com.meti.io.Source;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
import com.meti.io.connect.connections.Connection;
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
        ConnectionHandler handler = new ConnectionHandler() {
            @Override
            public Boolean handleImpl(Connection obj) {
                return true;
            }
        };
        peer = new Peer(handler);
    }

    @AfterEach
    void after() throws IOException {
        peer.close();
    }

    @Test
    void listen() throws IOException {
        ConnectionListener listener = peer.listen(0, SimpleConnection.class);
        Assertions.assertNotNull(listener);
    }

    @Test
    void initConnection() throws Exception {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);

        Source source = new Source(inputStream, outputStream);
        Connection connection = new Connection(source);
        Assertions.assertTrue(peer.initConnection(connection).get());
    }
}