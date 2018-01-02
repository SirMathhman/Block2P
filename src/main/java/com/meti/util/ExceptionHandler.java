package com.meti.util;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public interface ExceptionHandler extends Handler<Exception, Void> {
    @Override
    Void handle(Exception obj);
}
