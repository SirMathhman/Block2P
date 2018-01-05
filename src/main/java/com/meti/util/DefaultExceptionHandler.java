package com.meti.util;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class DefaultExceptionHandler extends ExceptionHandler {
    @Override
    public Void handleImpl(Exception obj) {
        obj.printStackTrace();
        return null;
    }
}
