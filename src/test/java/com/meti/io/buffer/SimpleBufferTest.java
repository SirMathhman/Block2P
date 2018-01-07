package com.meti.io.buffer;

import com.meti.io.Peer;
import com.meti.io.Source;
import com.meti.io.Sources;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
import com.meti.io.connect.connections.Connection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
class SimpleBufferTest {
    private SimpleBuffer buffer1;
    private SimpleBuffer buffer2;

    @Test
    public void test() throws IOException {
        ConnectionHandler handler1 = new Handler1();
        Peer peer1 = new Peer(handler1);
        ConnectionListener listener = peer1.listen(0, Connection.class);

        InetAddress address = InetAddress.getByName("localhost");
        int port = listener.getServerSocket().getLocalPort();
        Source socketSource = Sources.fromSocket(new Socket(address, port));

        ConnectionHandler handler2 = new Handler2();
        Peer peer2 = new Peer(handler2);
        peer2.initConnection(new Connection(socketSource));

        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            buffer1.set(20, 100);

            Assertions.assertEquals(100, buffer2.get(20));
        });
    }

    private class Handler1 extends ConnectionHandler {
        @Override
        public Boolean handleImpl(Connection obj) {
            buffer1 = new SimpleBuffer(obj);
            buffer1.synchronize();

            return true;
        }
    }

    private class Handler2 extends ConnectionHandler {
        @Override
        public Boolean handleImpl(Connection obj) {
            buffer2 = new SimpleBuffer(obj);
            buffer2.synchronize();

            return true;
        }
    }
}