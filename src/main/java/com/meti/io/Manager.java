package com.meti.io;

import com.meti.io.buffer.ObjectBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/9/2018
 */
public class Manager {
    private final ObjectBuffer buffer;

    public Manager(ObjectBuffer buffer) {
        this.buffer = buffer;
    }

    public <T> List<T> getAll(Class<T> c) {
        List<T> list = new ArrayList<>();
        for (Object obj : buffer) {
            if (c.isAssignableFrom(obj.getClass())) {
                list.add(c.cast(obj));
            }
        }

        return list;
    }
}
