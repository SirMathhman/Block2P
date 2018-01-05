package com.meti.buffer;

import com.meti.connect.*;
import com.meti.io.Source;
import com.meti.io.Sources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
class ObjectBufferTest {
    private ObjectBuffer<Object> buffer1;
    private ObjectBuffer<Object> buffer2;

    @Test
    public void test() throws IOException {
        ConnectionHandler handler1 = new Handler1();
        Peer peer1 = new Peer(handler1);
        ConnectionListener listener = peer1.listen(0, ObjectConnection.class);

        InetAddress address = InetAddress.getByName("localhost");
        int port = listener.getServerSocket().getLocalPort();
        Source socketSource = Sources.fromSocket(new Socket(address, port));

        ConnectionHandler handler2 = new Handler2();
        Peer peer2 = new Peer(handler2);
        peer2.initConnection(new ObjectConnection(socketSource));

        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            buffer1.add("Hello World");

            boolean result;
            do {
                result = buffer2.size() != 0;
            } while (result);
            Assertions.assertEquals("Hello World", buffer2.get(0, true));
        });
    }

    private class Handler1 implements ConnectionHandler {
        @Override
        public Boolean handle(Connection obj) throws IOException {
            buffer1 = new ObjectBuffer<>(new ObjectConnection(obj));
            buffer1.synchronize();

            return true;
        }
    }

    private class Handler2 implements ConnectionHandler {
        @Override
        public Boolean handle(Connection obj) throws IOException {
            buffer2 = new ObjectBuffer<>(new ObjectConnection(obj));
            buffer2.synchronize();

            return true;
        }
    }
}