package com.meti.io;

import com.meti.io.buffer.ObjectBuffer;
import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.ConnectionListener;
import com.meti.io.connect.connections.Connection;
import com.meti.io.connect.connections.ObjectConnection;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.Socket;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/9/2018
 */
class ManagerTest {
    @Test
    void test() throws Exception {
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
        @Override
        public Object handleThrows(Connection obj) {
            ObjectBuffer buffer = new ObjectBuffer((ObjectConnection) obj);
            buffer.open();
            return true;
        }
    }

    private class Handler2 extends ConnectionHandler {
        @Override
        public Object handleThrows(Connection obj) {
            ObjectBuffer buffer = new ObjectBuffer((ObjectConnection) obj);
            buffer.open();
            return true;
        }
    }
}