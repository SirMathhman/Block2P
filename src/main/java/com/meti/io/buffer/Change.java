package com.meti.io.buffer;

import java.io.Serializable;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
public class Change implements Serializable {
    private final Object object;
    private final int index;

    public Change(int index, Object object) {
        this.index = index;
        this.object = object;
    }

    public int getIndex() {
        return index;
    }

    public Object getObject() {
        return object;
    }
}
