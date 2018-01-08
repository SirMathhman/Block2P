package com.meti.io.buffer;

import com.meti.io.connect.connections.ObjectConnection;
import com.meti.util.Loop;
import com.meti.util.handle.Handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
public abstract class Buffer<T> {
    protected final ObjectConnection connection;
    private final HashMap<BufferOperation, Handler<T, Object>> handlerMap = new HashMap<>();
    private final HashSet<T> set = new HashSet<>();
    private final Class<T> tClass;
    protected boolean isSynchronized;

    //class
    {
        handlerMap.put(BufferOperation.ADD, new Handler<T, Object>() {
            @Override
            public Object handleImpl(T obj) {
                return set.add(obj);
            }
        });
        handlerMap.put(BufferOperation.REMOVE, new Handler<T, Object>() {
            @Override
            public Object handleImpl(T obj) {
                return set.remove(obj);
            }
        });
        handlerMap.put(BufferOperation.CLEAR, new Handler<T, Object>() {
            @Override
            public Object handleImpl(T obj) {
                set.clear();
                return null;
            }
        });
    }

    public Buffer(ObjectConnection connection, Class<T> tClass) {
        this.connection = connection;
        this.tClass = tClass;
    }

    public int size() {
        return set.size();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public boolean contains(T o) {
        return set.contains(o);
    }

    //changing the buffer
    public boolean add(T t) throws Exception {
        if (!isSynchronized()) {
            throw new IllegalStateException("Connections are not synchronized.");
        }

        Object to = update(BufferOperation.ADD, t);
        Object from = set.add(t);
        return !from.equals(to);
    }

    public Object update(BufferOperation operation, T obj) throws Exception {
        connection.writeObject(operation);
        connection.writeUnshared(obj);
        connection.flush();

        Object result = connection.readObject();
        if (result instanceof Exception) {
            throw (Exception) result;
        } else {
            return result;
        }
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public boolean remove(T o) throws Exception {
        if (!isSynchronized()) {
            throw new IllegalStateException("Connections are not synchronized.");
        }

        Object to = update(BufferOperation.REMOVE, o);
        Object from = set.remove(o);
        return !from.equals(to);
    }

    public void clear() throws Exception {
        if (!isSynchronized()) {
            throw new IllegalStateException("Connections are not synchronized.");
        }

        update(BufferOperation.CLEAR, null);
        set.clear();
    }

    public void synchronize() {
        synchronize(null);
    }

    public void synchronize(ExecutorService service) {
        BufferLoop loop = new BufferLoop();
        if (service == null) {
            new Thread(loop).start();
        } else {
            service.submit(loop);
        }
    }

    //anonymous
    private enum BufferOperation {
        ADD,
        REMOVE,
        CLEAR
    }

    private class BufferLoop extends Loop {
        @Override
        protected void init() {
            isSynchronized = true;
        }

        @Override
        protected void loop() throws Exception {
            BufferOperation operation = (BufferOperation) connection.readObject();
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
