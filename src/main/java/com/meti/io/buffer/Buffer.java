package com.meti.io.buffer;

import com.meti.io.connect.connections.ObjectConnection;
import com.meti.util.Loop;
import com.meti.util.event.EventManager;
import com.meti.util.handle.Handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
public class Buffer<T> {
    private final Set<ObjectConnection> connectionSet = new HashSet<>();
    private final HashMap<BUFFER_OPERATION, Handler<T, Object>> handlerMap = new HashMap<>();
    private final HashSet<T> contents = new HashSet<>();
    private final Class<T> tClass;

    private final EventManager manager = new EventManager();
    private boolean open;
    private Loop loop;

    //class
    {
        handlerMap.put(BUFFER_OPERATION.ADD, new Handler<T, Object>() {
            @Override
            public Object handleImpl(T obj) {
                return addImpl(obj);
            }
        });
        handlerMap.put(BUFFER_OPERATION.REMOVE, new Handler<T, Object>() {
            @Override
            public Object handleImpl(T obj) {
                return removeImpl(obj);
            }
        });
        handlerMap.put(BUFFER_OPERATION.CLEAR, new Handler<T, Object>() {
            @Override
            public Object handleImpl(T obj) {
                clearImpl();
                return null;
            }
        });
    }

    public Buffer(Class<T> tClass, ObjectConnection... connections) {
        this.connectionSet.addAll(Arrays.asList(connections));
        this.tClass = tClass;
    }

    public EventManager getManager() {
        return manager;
    }

    //changing the buffer
    public boolean add(T t) throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connections are not open.");
        }

        Object to = update(BUFFER_OPERATION.ADD, t);
        Object from = addImpl(t);
        return !from.equals(to);
    }

    private boolean addImpl(T e) {
        manager.handle(EVENT.ON_ADD, e);

        return contents.add(e);
    }

    public boolean isOpen() {
        return open;
    }

    private Object update(BUFFER_OPERATION operation, T obj) throws Exception {
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

    public boolean remove(T o) throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connections are not open.");
        }

        Object to = update(BUFFER_OPERATION.REMOVE, o);
        Object from = removeImpl(o);
        return !from.equals(to);
    }

    private boolean removeImpl(T e) {
        manager.handle(EVENT.ON_REMOVE, e);

        return contents.remove(e);
    }

    public void clear() throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connections are not open.");
        }

        update(BUFFER_OPERATION.CLEAR, null);
        clearImpl();
    }

    private void clearImpl() {
        manager.handle(EVENT.ON_CLEAR);

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

    public boolean contains(T o) {
        return contents.contains(o);
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public int size() {
        return contents.size();
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

    public void close() {
        loop.setRunning(false);
    }


    //anonymous
    public enum EVENT {
        ON_REMOVE, ON_CLEAR, ON_ADD
    }

    private enum BUFFER_OPERATION {
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
                        BUFFER_OPERATION operation = (BUFFER_OPERATION) token;
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
