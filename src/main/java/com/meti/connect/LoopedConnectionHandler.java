package com.meti.connect;

import com.meti.util.Loop;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/4/2018
 */
public abstract class LoopedConnectionHandler extends Loop implements ConnectionHandler {
    protected Connection connection;

    @Override
    public Boolean handle(Connection obj) {
        this.connection = obj;

        new Thread(this).start();

        return true;
    }
}
