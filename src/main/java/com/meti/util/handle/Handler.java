package com.meti.util.handle;

/**
 * Indicates to handle specified objects of type P.
 * P is of single object, but an array can be sent through.
 *
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public abstract class Handler<P, R> {
    private boolean handling;

    /**
     * Invokes the handler and handles the specified parameter P returning the result R.
     *
     * @param parameter The parameter.
     * @return The result.
     */
    public R handle(P parameter) {
        handling = true;
        R result = handleImpl(parameter);
        handling = false;

        return result;
    }

    /**
     * The class to implement.
     *
     * @param obj The parameter.
     * @return The result.
     */
    protected abstract R handleImpl(P obj);

    /**
     * Returns if the handler is handling.
     *
     * @return If the handler is handling.
     */
    public boolean isHandling() {
        return handling;
    }
}