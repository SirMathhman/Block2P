package com.meti.buffer;

import com.meti.connect.Connection;
import com.meti.util.Loop;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public class SimpleBuffer {
    private final int[] buffer = new int[256];
    private final Connection connection;

    //TODO: multiple connections
    public SimpleBuffer(Connection connection) {
        this.connection = connection;
    }

    public void synchronize() {
        synchronize(null);
    }

    public void synchronize(ExecutorService service) {
        if (service == null) {
            new Thread(new ReadLoop()).start();
        } else {
            service.submit(new ReadLoop());
        }
    }

    public void set(int index, int value) throws IOException {
        buffer[index] = value;

        update(index);
    }

    private void update(int... indexes) throws IOException {
        for (int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            int value = buffer[index];

            connection.write(index);
            connection.write(value);
        }

        connection.flush();
    }

    public int get(int index) {
        return buffer[index];
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