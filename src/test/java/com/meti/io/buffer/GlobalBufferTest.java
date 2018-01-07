package com.meti.io.buffer;

import com.meti.io.Peer;
import com.meti.io.Source;
import com.meti.io.Sources;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
import com.meti.io.connect.connections.Connection;
import com.meti.util.event.EventHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/7/2018
 */
public class GlobalBufferTest {
    private GlobalBuffer buffer1;
    private SimpleBuffer buffer2;

    @RepeatedTest(5)
    public void test() throws IOException {
        buffer1 = new GlobalBuffer();

        ConnectionHandler handler1 = new Handler1();
        Peer peer1 = new Peer(handler1);
        peer1.getManager().put(Peer.PROPERTIES.ON_INIT_CONNECTION, new EventHandler() {
            @Override
            public Void handleImpl(Object obj) {
                Object[] args = (Object[]) obj;
                buffer1.addConnection((Connection) args[1]);
                return null;
            }
        });
        ConnectionListener listener = peer1.listen(0, Connection.class);

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
                buffer1.synchronize();
                buffer1.set(20, 100);

                Assertions.assertEquals(100, buffer2.get(20));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
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
