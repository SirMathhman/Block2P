package com.meti.buffer;

import com.meti.connect.connections.Connection;
import com.meti.util.Loop;

import java.io.IOException;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class SimpleBuffer extends Buffer {
    private final int[] buffer = new int[256];
    private final Connection connection;

    //TODO: multiple connections
    public SimpleBuffer(Connection connection) {
        this.connection = connection;
    }

    public void set(int index, int value) throws IOException {
        buffer[index] = value;

        update(index);
    }

    @Override
    protected Loop getLoop() {
        return new ReadLoop();
    }

    public int get(int index) {
        return buffer[index];
    }

    private void update(int... indexes) throws IOException {
        if (!isSynched) {
            throw new IllegalStateException("Buffers aren't synchronized.");
        }

        for (int index : indexes) {
            int value = buffer[index];

            connection.write(index);
            connection.write(value);
        }

        connection.flush();
    }

    private class ReadLoop extends Loop {
        @Override
        protected void loop() throws Exception {
            int index = connection.read();
            int value = connection.read();

            buffer[index] = value;
        }
    }
}