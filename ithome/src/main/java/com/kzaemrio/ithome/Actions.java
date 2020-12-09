package com.kzaemrio.ithome;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface Actions {

    ExecutorService pool = Executors.newFixedThreadPool(1);
}
