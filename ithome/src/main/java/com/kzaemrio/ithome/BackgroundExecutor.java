package com.kzaemrio.ithome;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class BackgroundExecutor {

    @Inject
    ExecutorService pool;

    @Inject
    public BackgroundExecutor() {
    }

    public void executeOnBackground(Runnable runnable) {
        pool.execute(runnable);
    }
}
