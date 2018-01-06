package com.meti.io.buffer;

import com.meti.util.Loop;

import java.util.concurrent.ExecutorService;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
public abstract class Buffer {
    //handle this
    protected boolean isSynched;

    public void synchronize() {
        synchronize(null);
    }

    public void synchronize(ExecutorService service) {
        if (service == null) {
            new Thread(getLoop()).start();
        } else {
            service.submit(getLoop());
        }

        isSynched = true;
    }

    protected abstract Loop getLoop();

    public boolean isSynched() {
        return isSynched;
    }
}