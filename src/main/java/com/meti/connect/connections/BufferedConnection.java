package com.meti.connect.connections;

import com.meti.io.Source;
import com.meti.util.DefaultExceptionHandler;
import com.meti.util.ExceptionHandler;
import com.meti.util.Loop;

import java.util.PriorityQueue;
import java.util.Queue;

//TODO: documentation
public class BufferedConnection extends Connection {
    //not sure if we should use a priority queue here
    private final Queue<Integer> queue = new PriorityQueue<>();

    public BufferedConnection(Source source) {
        this(source, new DefaultExceptionHandler());
    }

    public BufferedConnection(Source source, ExceptionHandler handler) {
        super(source);

        new Thread(startQueueThread(handler));
    }

    private Runnable startQueueThread(ExceptionHandler handler) {
        return new QueueThread(handler);
    }

    /**
     * Reads a single byte from the internal queue.
     * If the queue does not contain any bytes, the Thread waits until there is at least one byte available.
     *
     * @return The byte.
     */
    @Override
    public int read() {
        boolean shouldLoop;
        do {
            shouldLoop = queue.size() == 0;
        } while (shouldLoop);

        return queue.poll();
    }

    private class QueueThread extends Loop {
        public QueueThread(ExceptionHandler handler) {
            super(handler);
        }

        @Override
        protected void loop() throws Exception {
            queue.add(BufferedConnection.super.read());
        }
    }
}