package com.meti.util.handle;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/9/2018
 */
public class BufferedExceptionHandler extends ExceptionHandler {
    private Exception mostRecent;

    //executed on exception thread
    @Override
    public Void handleImpl(Exception obj) {
        mostRecent = obj;
        return null;
    }

    //executed on listener thread
    public Exception getMostRecent() {
        return mostRecent;
    }
}
