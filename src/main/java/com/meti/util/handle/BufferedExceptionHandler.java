package com.meti.util.handle;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/9/2018
 */
public class BufferedExceptionHandler extends ExceptionHandler {
    private final Queue<Exception> exceptionQueue = new PriorityQueue<>();

    //executed on exception thread
    @Override
    public Void handleImpl(Exception obj) {
        exceptionQueue.add(obj);
        return null;
    }

    //executed on listener thread
    public boolean hasNextException() {
        return exceptionQueue.size() > 0;
    }

    public Exception getNextException() {
        return exceptionQueue.poll();
    }

    public List<Exception> asList() {
        return Arrays.asList((Exception[]) exceptionQueue.toArray());
    }
}
