package com.meti.util.handle;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/9/2018
 */
public abstract class ThrowableHandler<P, R> extends Handler<P, R> {
    private final BufferedExceptionHandler handler = new BufferedExceptionHandler();

    @Override
    public R handleImpl(P obj) {
        try {
            return handleThrows(obj);
        } catch (Exception e) {
            handler.handle(e);
            return null;
        }
    }

    public abstract R handleThrows(P obj) throws Exception;

    public BufferedExceptionHandler getHandler() {
        return handler;
    }
}
