package com.meti.util;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public class DefaultExceptionHandler implements ExceptionHandler {
    @Override
    public Void handle(Exception obj) {
        obj.printStackTrace();
        return null;
    }
}