package com.meti.io.buffer;

import com.meti.io.connect.connections.ObjectConnection;

/**
 * <p>
 *     ObjectBuffers can send back and forth objects.
 * </p>
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/8/2018
 */
public class ObjectBuffer extends Buffer<Object> {
    public ObjectBuffer(ObjectConnection... connections) {
        super(Object.class, connections);
    }
}
