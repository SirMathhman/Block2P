package com.meti.io;

import com.meti.io.buffer.Buffer;
import com.meti.io.buffer.ObjectBuffer;
import com.meti.util.event.EventHandler;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/9/2018
 */
public class Manager {
    private final HashMap<Object, Collection<?>> collectionHashMap = new HashMap<>();
    private final ObjectBuffer buffer;

    public Manager(ObjectBuffer buffer) {
        this.buffer = buffer;

        buffer.getManager().put(Buffer.EVENT.ON_ADD, new EventHandler() {
            @Override
            public Void handleImpl(Object obj) {
                getCollection(obj).add(obj);
                return null;
            }
        });

        buffer.getManager().put(Buffer.EVENT.ON_REMOVE, new EventHandler() {
            @Override
            public Void handleImpl(Object obj) {
                getCollection(obj).remove(obj);
                return null;
            }
        });

        buffer.getManager().put(Buffer.EVENT.ON_CLEAR, new EventHandler() {
            @Override
            public Void handleImpl(Object obj) {
                if (obj != null) {
                    throw new IllegalStateException("Found a parameter for clearing the buffer in the Manager class!");
                }

                collectionHashMap.values().forEach(Collection::clear);

                return null;
            }
        });
    }

    public <T> Collection<T> getCollection(T obj) {
        return (Collection<T>) collectionHashMap.get(obj);
    }

    public void bind(Collection<?> collection) {
        for (Object obj : collection) {
            collectionHashMap.put(obj, collection);
        }
    }
}
