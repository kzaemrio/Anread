package com.kzaemrio.ithome;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

@Module
@InstallIn(ApplicationComponent.class)
public class AppModule {
    @Provides
    public static ExecutorService executorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    public static okhttp3.OkHttpClient okHttpClient(ExecutorService pool) {
        return new OkHttpClient.Builder().dispatcher(new Dispatcher(pool)).build();
    }
}
