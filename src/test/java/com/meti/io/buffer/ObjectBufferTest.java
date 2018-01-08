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
import java.time.Duration;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
class ObjectBufferTest {
    private ObjectBuffer buffer2;

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
        peer2.initConnection(new ObjectConnection(socketSource));
    }

    private class Handler1 extends ConnectionHandler {
        @Override
        public Boolean handleImpl(Connection obj) {
            boolean toReturn;
            try {
                Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
                    ObjectBuffer buffer1 = new ObjectBuffer(new ObjectConnection(obj));

                    buffer1.open();

                    buffer1.awaitUntilSynchronized();
                    buffer1.add("Hello World");

                    Assertions.assertTrue(buffer2.contains("Hello World"));

                    buffer1.close();
                });
                toReturn = true;
            } catch (Exception e) {
                e.printStackTrace();

                toReturn = false;
            } finally {
                try {
                    obj.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return toReturn;
        }
    }

    private class Handler2 extends ConnectionHandler {
        @Override
        public Boolean handleImpl(Connection obj) {
            boolean toReturn;
            try {
                buffer2 = new ObjectBuffer(new ObjectConnection(obj));
                buffer2.open();

                toReturn = true;
                //buffer2 closed on other end by buffer1
            } catch (IOException e) {
                e.printStackTrace();
                toReturn = false;
            } finally {
                try {
                    obj.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return toReturn;
        }
    }
}