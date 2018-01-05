package com.meti.connect.connections;

import com.meti.io.Source;
import com.meti.util.DefaultExceptionHandler;
import com.meti.util.ExceptionHandler;
import com.meti.util.Loop;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

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

    public BufferedConnection(Source source, ExecutorService service) {
        this(source, service, new DefaultExceptionHandler());
    }

    public BufferedConnection(Source source, ExecutorService service, ExceptionHandler handler) {
        super(source);

        service.submit(startQueueThread(handler));
    }

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
