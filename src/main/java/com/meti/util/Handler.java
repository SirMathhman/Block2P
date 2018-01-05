package com.meti.util;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public abstract class Handler<P, R> {
    private boolean handling;

    public R handle(P parameter) throws Exception {
        handling = true;
        R result = handleImpl(parameter);
        handling = false;

        return result;
    }

    public abstract R handleImpl(P obj) throws Exception;

    public boolean isHandling() {
        return handling;
    }
}