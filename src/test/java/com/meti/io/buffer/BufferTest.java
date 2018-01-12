package com.meti.io.buffer;

import com.meti.io.Peer;
import com.meti.io.Source;
import com.meti.io.Sources;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
import com.meti.io.connect.connections.Connection;
import com.meti.io.connect.connections.ObjectConnection;
import com.meti.util.handle.BufferedExceptionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.Socket;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
class BufferTest {
    private SimpleBuffer buffer2;

    @Test
    public void test() throws Exception {
        Handler1 handler1 = new Handler1();
        Peer peer1 = new Peer(handler1);
        ConnectionListener listener = peer1.listen(0, ObjectConnection.class);

        InetAddress address = InetAddress.getByName("localhost");
        int port = listener.getServerSocket().getLocalPort();
        Source socketSource = Sources.fromSocket(new Socket(address, port));

        Handler2 handler2 = new Handler2();
        Peer peer2 = new Peer(handler2);
        peer2.initConnection(new Connection(socketSource));

        if (handler1.getHandler().hasNextException()) {
            throw handler1.getHandler().getNextException();
        }

        if (handler2.getHandler().hasNextException()) {
            throw handler2.getHandler().getNextException();
        }
    }

    private class Handler1 extends ConnectionHandler {
        private final BufferedExceptionHandler handler = new BufferedExceptionHandler();

        @Override
        public Object handleThrows(Connection obj) throws Exception {
            SimpleBuffer buffer1 = new SimpleBuffer((ObjectConnection) obj);
            buffer1.open();

            buffer1.add(100);
            buffer1.add(3000);
            Assertions.assertTrue(buffer2.contains(100));

            buffer1.remove(3000);
            Assertions.assertFalse(buffer2.contains(3000));

            buffer1.clear();
            Assertions.assertTrue(buffer2.isEmpty());

            return true;
        }
    }

    private class Handler2 extends ConnectionHandler {
        @Override
        public Object handleThrows(Connection obj) {
            buffer2 = new SimpleBuffer((ObjectConnection) obj);
            buffer2.open();

            return true;
        }
    }
}