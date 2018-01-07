package com.meti.io.connect.connections;

import com.meti.io.Source;
import com.meti.util.handle.DefaultExceptionHandler;
import com.meti.util.handle.ExceptionHandler;
import com.meti.util.Loop;

import java.util.PriorityQueue;
import java.util.Queue;

public class BufferedConnection extends Connection {
    //not sure if we should use a priority queue here
    private final Queue<Integer> queue = new PriorityQueue<>();

    /**
     * Constructs a BufferedConnection from a Source and with the DefaultExceptionHandler.
     *
     * @param source The source.
     * @see DefaultExceptionHandler
     */
    public BufferedConnection(Source source) {
        this(source, new DefaultExceptionHandler());
    }

    /**
     * Constructs a BufferedConnection from a Source and an ExceptionHandler.
     * If an Exception is thrown when reading from the stream into the queue,
     * it is pushed to the handler.
     * @param source  The source.
     * @param handler The handler.
     */
    public BufferedConnection(Source source, ExceptionHandler handler) {
        super(source);

        new Thread(startQueueThread(handler));
    }

    public boolean hasData() {
        return !queue.isEmpty();
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