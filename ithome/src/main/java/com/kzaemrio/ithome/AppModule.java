package com.kzaemrio.ithome;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class AppModule {
    @Provides
    public static ExecutorService executorService() {
        return Executors.newFixedThreadPool(1);
    }
}
