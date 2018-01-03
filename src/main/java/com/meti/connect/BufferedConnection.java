package com.meti.connect;

import com.meti.util.DefaultExceptionHandler;
import com.meti.util.ExceptionHandler;
import com.meti.util.Loop;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class BufferedConnection extends Connection {
    //not sure if we should use a priority queue here
    private final Queue<Integer> queue = new PriorityQueue<>();

    public BufferedConnection(InputStream inputStream, OutputStream outputStream) {
        this(inputStream, outputStream, new DefaultExceptionHandler());
    }

    public BufferedConnection(InputStream inputStream, OutputStream outputStream, ExceptionHandler handler) {
        super(inputStream, outputStream);

        new Thread(startQueueThread(handler));
    }

    public BufferedConnection(InputStream inputStream, OutputStream outputStream, ExecutorService service) {
        this(inputStream, outputStream, service, new DefaultExceptionHandler());
    }

    public BufferedConnection(InputStream inputStream, OutputStream outputStream, ExecutorService service, ExceptionHandler handler) {
        super(inputStream, outputStream);

        service.submit(startQueueThread(handler));
    }

    private Runnable startQueueThread(ExceptionHandler handler) {
        return new QueueThread(handler);
    }

    @Override
    public int read() {
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
