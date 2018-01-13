package com.meti.io.buffer;

import com.meti.io.connect.connections.ObjectConnection;

/**
 * <p>
 *     SimpleBuffers can send back and forth bytes.
 * </p>
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class SimpleBuffer extends Buffer<Integer> {
    public SimpleBuffer(ObjectConnection... connections) {
        super(Integer.class, connections);
    }
}