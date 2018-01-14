package com.meti.util.event;

import com.meti.util.handle.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * The EventManager activates specified Handlers in accordance to an enumeration.
 * An EventManager is typically provided by another class, via getManager().
 * Retrieving the EventManager allows for adding handlers to react to internal events.
 * The EventManager internally uses a HashMap.
 * <p>
 * </p>
 *
 * @author SirMathhman
 * @version 0.0.0
 * @see EventHandler
 * @see HashMap
 * @see Handler
 * @since 1/7/2018
 */
public class EventManager {
    private final HashMap<Enum<?>, List<EventHandler>> eventMap = new HashMap<>();

    /**
     * Associates a handler and an enumeration constant into the internal event map.
     * Multiple associates can be put for a single key.
     *
     * @param key   The key.
     * @param value The handler.
     */
    public void put(Enum<?> key, EventHandler value) {
        getList(key).add(value);
    }

    /**
     * Gets all handlers associated with a specified key.
     *
     * @param key The key.
     * @return The list.
     */
    public List<EventHandler> getList(Enum<?> key) {
        List<EventHandler> list;
        if (eventMap.containsKey(key)) {
            list = eventMap.get(key);
        } else {
            list = new ArrayList<>();
            eventMap.put(key, list);
        }
        return list;
    }

    /**
     * Removes an entire list from being associated from a key.
     *
     * @param key The key.
     * @return The removed list.
     */
    public List<EventHandler> removeList(Enum<?> key) {
        return eventMap.remove(key);
    }

    /**
     * Removes a single handler from being associated from a key.
     *
     * @param key     The key.
     * @param handler The handler.
     * @return If the associated handler was found and removed, or if no associations were found for the key.
     */
    public boolean remove(Enum<?> key, EventHandler handler) {
        List<EventHandler> list = getList(key);
        if (list != null) {
            return list.remove(handler);
        } else {
            return true;
        }
    }

    /**
     * Executes handlers associated from a given key and parameters.
     * The same parameters will be passed through every handler.
     *
     * @param key        The key.
     * @param parameters The parameters to handle with.
     * @return If any associations were found for the key.
     */
    public boolean handle(Enum<?> key, Object... parameters) {
        List<EventHandler> handlers = getList(key);
        if (handlers != null) {
            for (Handler<Object, Void> handler : handlers) {
                if (handler != null) {
                    handler.handle(parameters);
                }
            }
            return true;
        } else {
            return false;
        }
    }
}