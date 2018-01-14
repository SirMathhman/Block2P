package com.meti.util.event;

/**
 * <p>
 * Indicates that this class or object uses an event manager.
 * This makes it easier to activate methods or actions whenever a specific event occurs in the supplying class.
 * </p>
 *
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/12/2018
 */
public interface Managable {
    /**
     * Returns this object's event manager.
     *
     * @return The event manager.
     */
    EventManager getManager();
}
