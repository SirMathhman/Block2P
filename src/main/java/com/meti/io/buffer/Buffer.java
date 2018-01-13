package com.meti.io.buffer;

import com.meti.io.ConnectionException;
import com.meti.io.connect.connections.Connection;
import com.meti.io.connect.connections.ObjectConnection;
import com.meti.util.Loop;
import com.meti.util.event.EventManager;
import com.meti.util.handle.Handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * A Buffer contains a Set of objects that can be shared across multiple connections.
 * The Buffer will become synchronized with other buffers.
 * </p>
 *
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
//class
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

    /**
     * <p>
     * Constructs a Buffer using an internal HashSet of type tClass and an initial array of connections.
     * tClass is the generic class that casts the incoming changes from other buffers.
     * The initial array of connections provided is not limited to be less than two.
     * These connections will be listened to when the {@link #open} method is declared.
     * </p>
     *
     * @param tClass      The generic class to instantiate.
     * @param connections The initial array of connections.
     * @see HashSet
     */
    public Buffer(Class<T> tClass, ObjectConnection... connections) {
        this.connectionSet.addAll(Arrays.asList(connections));
        this.tClass = tClass;
    }

    /**
     * Adds an array of connections to the internal set of connections.
     * The array of connections can have zero, one, or more than two elements.
     * These connections will then be listened to.
     *
     * @param connections The connections.
     * @return If the internal set changed successfully.
     */
    public boolean addConnections(ObjectConnection... connections) {
        return this.connectionSet.addAll(Arrays.asList(connections));
    }

    /**
     * Removes an array of connections from the internal set of connections.
     * The array of connections can have zero, one, or more than two elements.
     * These connections will then be no longer listened to.
     *
     * @param connections The connections.
     * @return If the internal set changed successfully.
     */
    public boolean removeConnections(ObjectConnection... connections) {
        return this.connectionSet.removeAll(Arrays.asList(connections));
    }

    /**
     * Clears the internal set of connections.
     * All connections will then no longer be listened to.
     */
    public void clearConnections() {
        this.connectionSet.clear();
    }

    /**
     * Gets the internal EventManager.
     *
     * @return The internal EventManager.
     */
    public EventManager getManager() {
        return manager;
    }

    //changing the buffer

    /**
     * Adds an object to the buffer.
     *
     * @param t The object.
     * @return If it was added successfully to all the buffers.
     * @throws Exception If an error occurred with updating the other buffers.
     */
    public boolean add(T t) throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connections are not open.");
        }

        Object to = update(BUFFER_OPERATION.ADD, t);
        Object from = addImpl(t);
        return !from.equals(to);
    }

    /**
     * Returns if the buffer is open and listening for connections.
     *
     * @return The state.
     */
    public boolean isOpen() {
        return open;
    }

    private boolean addImpl(T e) {
        manager.handle(EVENTS.ON_ADD, e);

        return contents.add(e);
    }

    private boolean update(BUFFER_OPERATION operation, T obj) throws Exception {
        boolean result = true;
        if (connectionSet.size() != 0) {
            for (ObjectConnection connection : connectionSet) {
                if (!connection.isClosed()) {
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
                } else {
                    throw new ConnectionException("Connection closed.");
                }
            }
        }

        return result;
    }

    /**
     * Closes the buffer.
     * The buffer stops listening from connections.
     * All connections that are not closed are terminated.
     *
     * @throws IOException If an Exception occurred with closing one of the connections.
     */
    public void close() throws IOException {
        loop.setRunning(false);

        for (Connection connection : connectionSet) {
            if (!connection.isClosed()) {
                connection.close();
            }
        }
    }

    /**
     * Removes an object from the buffer.
     *
     * @param o The object.
     * @return If it was successfully added to the other buffers.
     * @throws Exception If an Exception occurred with updating the other buffers.
     */
    public boolean remove(T o) throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connections are not open.");
        }

        Object to = update(BUFFER_OPERATION.REMOVE, o);
        Object from = removeImpl(o);
        return !from.equals(to);
    }

    private boolean removeImpl(T e) {
        manager.handle(EVENTS.ON_REMOVE, e);

        return contents.remove(e);
    }

    /**
     * Clears the buffer.
     *
     * @throws Exception If an Exception occurred with updating the other buffers.
     */
    public void clear() throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connections are not open.");
        }

        update(BUFFER_OPERATION.CLEAR, null);
        clearImpl();
    }

    private void clearImpl() {
        manager.handle(EVENTS.ON_CLEAR);

        contents.clear();
    }

    /**
     * Returns true if the buffer contains at least one of the items specified.
     *
     * @param items The items to search through.
     * @return The state.
     */
    public boolean containsOne(Set<T> items) {
        for (T item : items) {
            if (contains(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the buffer contains the item specified.
     *
     * @param o The item.
     * @return The state.
     */
    public boolean contains(T o) {
        return contents.contains(o);
    }

    /**
     * Returns true if the buffer is empty.
     *
     * @return The state.
     */
    public boolean isEmpty() {
        return contents.isEmpty();
    }

    /**
     * Returns the size of the buffer.
     *
     * @return The size of the buffer.
     */
    public int size() {
        return contents.size();
    }

    /**
     * Opens the buffer without using an ExecutorService,
     * and begins to listen for changes from other buffers.
     */
    public void open() {
        open(null);
    }

    /**
     * Opens the buffer using an ExecutorService,
     * and begins to listen for changes from other buffers.
     *
     * @param service The service.
     */
    public void open(ExecutorService service) {
        loop = new BufferLoop();
        if (service == null) {
            new Thread(loop).start();
        } else {
            service.submit(loop);
        }
    }


//anonymous

    /**
     * Types of Events that the Buffer can handle.
     *
     * @see {@link #getManager()}
     */
    public enum EVENTS {
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

    private class Change implements Serializable {
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
}
