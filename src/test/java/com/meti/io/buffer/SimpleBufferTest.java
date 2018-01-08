package com.meti.io.buffer;

import com.meti.io.Peer;
import com.meti.io.Source;
import com.meti.io.Sources;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
import com.meti.io.connect.connections.Connection;
import com.meti.io.connect.connections.ObjectConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
class SimpleBufferTest {
    private SimpleBuffer buffer2;

    @RepeatedTest(5)
    public void test() throws IOException {
        ConnectionHandler handler1 = new Handler1();
        Peer peer1 = new Peer(handler1);
        ConnectionListener listener = peer1.listen(0, ObjectConnection.class);

        InetAddress address = InetAddress.getByName("localhost");
        int port = listener.getServerSocket().getLocalPort();
        Source socketSource = Sources.fromSocket(new Socket(address, port));

        ConnectionHandler handler2 = new Handler2();
        Peer peer2 = new Peer(handler2);
        peer2.initConnection(new Connection(socketSource));
    }

    private class Handler1 extends ConnectionHandler {
        @Override
        public Boolean handleImpl(Connection obj) {
            try {
                SimpleBuffer buffer1 = new SimpleBuffer(new ObjectConnection(obj));
                buffer1.open();

                buffer1.add(100);

                Assertions.assertTrue(buffer2.contains(100));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private class Handler2 extends ConnectionHandler {
        @Override
        public Boolean handleImpl(Connection obj) {
            try {
                buffer2 = new SimpleBuffer(new ObjectConnection(obj));
                buffer2.open();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}