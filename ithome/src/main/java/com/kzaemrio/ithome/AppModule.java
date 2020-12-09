package com.kzaemrio.ithome;

import android.app.Application;

import androidx.room.Room;

import com.kzaemrio.ithome.db.AppDataBase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

@Module
@InstallIn(ApplicationComponent.class)
public class AppModule {
    @Singleton
    @Provides
    public static ExecutorService executorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Singleton
    @Provides
    public static okhttp3.OkHttpClient okHttpClient(ExecutorService pool) {
        return new OkHttpClient.Builder().dispatcher(new Dispatcher(pool)).build();
    }

    @Singleton
    @Provides
    public static AppDataBase appDataBase(Application application, ExecutorService pool) {
        return Room.databaseBuilder(application, AppDataBase.class, "db")
                .setQueryExecutor(pool)
                .setTransactionExecutor(pool)
                .build();
    }
}
