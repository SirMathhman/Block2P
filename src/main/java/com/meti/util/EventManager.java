package com.meti.util;

import java.util.HashMap;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/7/2018
 */
public class EventManager {
    private final HashMap<Enum<?>, Handler<Object, Void>> eventMap = new HashMap<>();

    public void put(Enum<?> key, Handler<Object, Void> value) {
        eventMap.put(key, value);
    }

    public void handle(Enum<?> key, Object... parameters) {
        Handler<Object, Void> handler = get(key);
        if (handler != null) {
            handler.handle(parameters);
        }
    }

    public Handler<Object, Void> get(Enum<?> key) {
        return eventMap.get(key);
    }
}
