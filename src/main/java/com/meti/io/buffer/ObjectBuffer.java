package com.meti.io.buffer;

import com.meti.io.connect.connections.ObjectConnection;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/8/2018
 */
public class ObjectBuffer extends Buffer<Object> {
    public ObjectBuffer(Class<Object> objectClass, ObjectConnection... connections) {
        super(objectClass, connections);
    }
}
