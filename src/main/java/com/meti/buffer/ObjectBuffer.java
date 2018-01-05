package com.meti.buffer;

import com.meti.connect.connections.ObjectConnection;
import com.meti.util.Loop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
public class ObjectBuffer<T> extends Buffer {
    private final ObjectConnection connection;
    private final List<T> content;

    public ObjectBuffer(ObjectConnection connection) {
        this(new ArrayList<>(), connection);
    }

    public ObjectBuffer(List<T> content, ObjectConnection connection) {
        this.content = content;
        this.connection = connection;
    }

    @Override
    protected Loop getLoop() {
        return new ReadLoop();
    }

    public int size() {
        return content.size();
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public boolean contains(T o) {
        return content.contains(o);
    }

    public void add(T o) throws IOException {
        content.add(o);

        update(content.size() - 1);
    }

    private void update(int... indexes) throws IOException {
        if (!isSynched) {
            throw new IllegalStateException("Buffers aren't synchronized.");
        }

        for (int index : indexes) {
            connection.writeUnshared(new Change(index, content.get(index)));
        }

        connection.flush();
    }

    public void remove(int index) throws IOException {
        content.remove(index);

        update();
    }

    public void clear() {
        content.clear();
    }

    public T get(int index) {
        return get(index, false);
    }

    public T get(int index, boolean wait) {
        if (wait) {
            boolean waiting;
            do {
                waiting = content.size() <= index;
            } while (waiting);
        }

        return content.get(index);
    }

    public T set(int index, T element) throws IOException {
        T result = content.set(index, element);

        update(index);
        return result;
    }

    public int indexOf(T o) {
        return content.indexOf(o);
    }

    public List<T> getContent() {
        return content;
    }

    private class ReadLoop extends Loop {
        @Override
        @SuppressWarnings("unchecked")
        protected void loop() throws Exception {
            Object obj = connection.readUnshared();
            if (obj instanceof Change) {
                Change change = (Change) obj;
                //unchecked cast, we don't have class type of T

                int index = ((Change) obj).getIndex();

                T object = (T) change.getObject();
                if (content.size() < index) {
                    content.set(index, object);
                } else {
                    content.add(index, object);
                }
            } else {
                throw new IllegalArgumentException("Cannot handle object of type " + obj.getClass());
            }
        }
    }
}
