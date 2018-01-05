package com.meti.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
class LoopTest {
    private static final int[] data = new int[]{1, 2, 3};
    private boolean success;

    @Test
    public void test() {
        initTest();
    }

    private void initTest() {
        Loop loop = new TestLoop(new ExceptionHandler() {
            @Override
            public Void handleImpl(Exception obj) {
                success = (obj != null);
                return null;
            }
        });

        Thread thread = new Thread(loop);
        thread.start();

        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            boolean next;
            do {
                next = success;
            } while (!next);
        });
    }

    private class TestLoop extends Loop {
        int sum = 0;
        int counter = 0;

        TestLoop(ExceptionHandler handler) {
            super(handler);
        }

        @Override
        protected void loop() throws Exception {
            sum += data[counter];

            if (sum == 6) {
                throw new Exception("Success!");
            }

            counter++;
        }
    }
}