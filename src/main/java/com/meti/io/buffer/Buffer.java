package com.meti.io.buffer;

import com.meti.io.connect.connections.ObjectConnection;
import com.meti.util.Loop;
import com.meti.util.handle.Handler;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
public class Buffer<T> implements Iterable<T> {
    private final Set<ObjectConnection> connectionSet = new HashSet<>();
    private final HashMap<BufferOperation, Handler<T, Object>> handlerMap = new HashMap<>();
    private final HashSet<T> contents = new HashSet<>();
    private final Class<T> tClass;

    private boolean open;
    private Loop loop;

    //class
    //class
    //class
//class
    //class
    //class
    //class
    {
        handlerMap.put(BufferOperation.ADD, new Handler<T, Object>() {
            @Override
            public Object handleImpl(T obj) {
                return contents.add(obj);
            }
        });
        handlerMap.put(BufferOperation.REMOVE, new Handler<T, Object>() {
            @Override
            public Object handleImpl(T obj) {
                return contents.remove(obj);
            }
        });
        handlerMap.put(BufferOperation.CLEAR, new Handler<T, Object>() {
            @Override
            public Object handleImpl(T obj) {
                contents.clear();
                return null;
            }
        });
    }

    public Buffer(Class<T> tClass, ObjectConnection... connections) {
        this.connectionSet.addAll(Arrays.asList(connections));
        this.tClass = tClass;
    }

    @Override
    public Iterator<T> iterator() {
        return contents.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        contents.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return contents.spliterator();
    }

    public int size() {
        return contents.size();
    }

    public Object update(BufferOperation operation, T obj) throws Exception {
        boolean result = true;
        for (ObjectConnection connection : connectionSet) {
            connection.writeObject(operation);
            connection.writeUnshared(obj);
            connection.flush();

            Object token = connection.readObject();
            if (token instanceof Exception) {
                throw (Exception) token;
            } else {
                if (token instanceof Boolean) {
                    result = result && (Boolean) token;
                } else {
                    throw new IllegalArgumentException("Don't know how to handle data of type " + token.getClass());
                }
            }
        }

        return result;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public boolean contains(T o) {
        return contents.contains(o);
    }

    public void open() {
        open(null);
    }

    public void open(ExecutorService service) {
        loop = new BufferLoop();
        if (service == null) {
            new Thread(loop).start();
        } else {
            service.submit(loop);
        }
    }

    public void awaitUntilSynchronized() {
        boolean shouldContinue;
        do {
            shouldContinue = !open;
        } while (shouldContinue);
    }

    public void close() {
        loop.setRunning(false);
    }

    //changing the buffer
    public boolean add(T t) throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connections are not open.");
        }

        Object to = update(BufferOperation.ADD, t);
        Object from = contents.add(t);
        return !from.equals(to);
    }

    public boolean remove(T o) throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connections are not open.");
        }

        Object to = update(BufferOperation.REMOVE, o);
        Object from = contents.remove(o);
        return !from.equals(to);
    }

    public void clear() throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connections are not open.");
        }

        update(BufferOperation.CLEAR, null);
        contents.clear();
    }

    public boolean containsOne(Set<T> items) {
        for (T item : items) {
            if (contents.contains(item)) {
                return true;
            }
        }

        return false;
    }

    //anonymous
    private enum BufferOperation {
        ADD,
        REMOVE,
        CLEAR,
    }

    private class BufferLoop extends Loop {
        @Override
        protected void start() {
            open = true;
        }

        @Override
        protected void loop() throws Exception {
            if (open) {
                for (ObjectConnection connection : connectionSet) {
                    if (!connection.isClosed() && connection.hasData()) {
                        Object token = connection.readObject();
                        BufferOperation operation = (BufferOperation) token;
                        Object obj = connection.readObject();
                        if (tClass.isAssignableFrom(obj.getClass())) {
                            T tObj = tClass.cast(obj);
                            Object result = handlerMap.get(operation).handle(tObj);
                            connection.writeObject(result);
                            connection.flush();
                        }
                    }
                }
            }
        }
    }
}
