package com.meti.io.buffer;

import com.meti.io.connect.connections.Connection;
import com.meti.util.Loop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/6/2018
 */
public class GlobalBuffer extends Buffer {
    private final ArrayList<Connection> connections = new ArrayList<>();
    private int[] buffer = new int[255];

    public GlobalBuffer() {
        this(null);
    }

    public GlobalBuffer(ExecutorService service) {
        super(t, tClass);
        if (service == null) {
            new Thread(getLoop()).start();
        } else {
            service.submit(this::getLoop);
        }
    }

    @Override
    protected Loop getLoop() {
        return new ReadLoop();
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    public void set(int index, int value) throws IOException {
        buffer[index] = value;

        update(index);
    }

    private void update(int... indexes) throws IOException {
        if (!isSynchronized) {
            throw new IllegalStateException("Buffers aren't synchronized.");
        }

        for (Connection connection : connections) {
            for (int index : indexes) {
                int value = buffer[index];

                connection.write(index);
                connection.write(value);
            }

            connection.flush();
        }
    }

    public int get(int index) {
        return buffer[index];
    }

    private class ReadLoop extends Loop {
        @Override
        protected void loop() throws IOException {
            if (connections.size() != 0) {
                for (Connection connection : connections) {
                    if (connection.hasData()) {
                        int index = connection.read();
                        int value = connection.read();

                        buffer[index] = value;
                    }
                }
            }
        }
    }
}
