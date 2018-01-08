package com.meti.io.buffer;

import com.meti.io.connect.connections.ObjectConnection;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class SimpleBuffer extends Buffer<Integer> {
    public SimpleBuffer(ObjectConnection... connections) {
        super(Integer.class, connections);
    }
}