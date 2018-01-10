package com.meti.io;

import com.meti.io.connect.ConnectionHandler;
import com.meti.io.connect.connections.Connection;
import org.junit.jupiter.api.Test;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/9/2018
 */
class ManagerTest {
    @Test
    void test() {
        ConnectionHandler handler = new ConnectionHandler() {
            @Override
            public Object handleImpl(Connection obj) {
                return null;
            }
        };
    }
}