package com.meti.util;

import com.meti.util.handle.DefaultExceptionHandler;
import com.meti.util.handle.ExceptionHandler;

/**
 * The Loop class implements Runnable to constantly execute a task specified by {@link #loop()}.
 * The behaviour of the Loop is documented under {@link #run()}
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public abstract class Loop implements Runnable {
    private final ExceptionHandler handler;

    /**
     * Constructs a Loop with a DefaultExceptionHandler.
     */
    public Loop() {
        this(new DefaultExceptionHandler());
    }

    /**
     * Constructs a Loop with a specified ExceptionHandler.
     *
     * @param handler The handler.
     */
    public Loop(ExceptionHandler handler) {
        this.handler = handler;
    }

    /**
     * <p>
     * Runs the thread.
     * The loop continues while the Thread hasn't been interrupted.
     * Each time, the {@link #loop()} method is called.
     * If an Exception is thrown, then it is called to the ExceptionHandler.
     * </p>
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                loop();
            } catch (Exception e) {
                try {
                    handler.handle(e);
                } catch (Exception e1) {
                    System.err.println("Cannot handle exception " + e1);
                    System.exit(-1);
                }
                break;
            }
        }
    }

    /**
     * Specifies the method or action to be declared continuously.
     *
     * @throws Exception If an Exception occurred.
     */
    protected abstract void loop() throws Exception;
}
