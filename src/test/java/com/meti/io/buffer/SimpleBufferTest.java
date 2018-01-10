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
class SimpleBufferTest {
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

        if (handler1.getExceptionHandler().hasNextException()) {
            throw handler1.getExceptionHandler().getNextException();
        }

        if (handler2.getExceptionHandler().hasNextException()) {
            throw handler2.getExceptionHandler().getNextException();
        }
    }

    private class Handler1 extends ConnectionHandler {
        private final BufferedExceptionHandler handler = new BufferedExceptionHandler();

        @Override
        public Boolean handleImpl(Connection obj) {
            try {
                buffer2 = new SimpleBuffer((ObjectConnection) obj);
                buffer2.open();
                return true;
            } catch (Exception e) {
                handler.handle(e);
                return false;
            }
        }

        public BufferedExceptionHandler getExceptionHandler() {
            return handler;
        }
    }

    private class Handler2 extends ConnectionHandler {
        private final BufferedExceptionHandler handler = new BufferedExceptionHandler();

        public BufferedExceptionHandler getExceptionHandler() {
            return handler;
        }

        @Override
        public Boolean handleImpl(Connection obj) {
            try {
                SimpleBuffer buffer1 = new SimpleBuffer(new ObjectConnection(obj));
                buffer1.open();
                buffer1.add(100);

                Assertions.assertTrue(buffer2.contains(100));

                return true;
            } catch (Exception e) {
                handler.handle(e);
                return false;
            }
        }


    }
}