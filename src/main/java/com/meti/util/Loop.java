package com.meti.util;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public abstract class Loop implements Runnable {
    private final ExceptionHandler handler;

    public Loop() {
        this(new DefaultExceptionHandler());
    }

    public Loop(ExceptionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                loop();
            } catch (Exception e) {
                handler.handle(e);
            }
        }
    }

    protected abstract void loop() throws Exception;
}
