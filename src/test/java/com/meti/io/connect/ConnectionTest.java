package com.meti.io.connect;

import com.meti.io.Peer;
import com.meti.io.Source;
import com.meti.io.connect.connections.Connection;
import com.meti.util.Loop;
import com.meti.util.handle.BufferedExceptionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;
import java.util.concurrent.Future;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
class ConnectionTest {
    private static final PipedInputStream inputStream = new PipedInputStream();
    private static final PipedOutputStream outputStream = new PipedOutputStream();

    private static Peer peer;

    //methods
    @Test
    void construct() throws Exception {
        inputStream.connect(outputStream);

        ConnectionTestRunnable runnable = new ConnectionTestRunnable();
        Thread runnableThread = new Thread(runnable);
        runnableThread.start();

        ConnectionHandler handler = new ConnectionHandler() {
            @Override
            public Object handleThrows(Connection connection) {
                try {
                    connection.write(100);
                    connection.flush();
                    Assertions.assertEquals(100, connection.read());

                    connection.close();
                    return true;
                } catch (IOException e) {
                    return e;
                }
            }
        };

        peer = new Peer(handler);
        Future<Object> connectionFuture = peer.initConnection(new Connection(new Source(inputStream, outputStream)));
        Assertions.assertEquals(true, connectionFuture.get());
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            peer.close();
            runnable.setRunning(false);
            if (runnableThread.isAlive()) {
                runnableThread.interrupt();
                runnableThread.join();
            }
        });

        Assertions.assertThrows(InterruptedIOException.class, () -> {
            BufferedExceptionHandler exceptionHandler = (BufferedExceptionHandler) runnable.getHandler();
            if (exceptionHandler.hasNextException()) {
                throw exceptionHandler.getNextException();
            }
        });
    }

    private static class ConnectionTestRunnable extends Loop {
        public ConnectionTestRunnable() {
            super(new BufferedExceptionHandler());
        }

        @Override
        protected void loop() throws IOException {
            int b = inputStream.read();
            outputStream.write(b);
            outputStream.flush();
        }
    }
}