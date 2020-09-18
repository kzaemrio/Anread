package com.kzaemrio.ithome.db;

import android.app.Application;

import androidx.room.Room;

import com.kzaemrio.ithome.Actions;

public class AppDataBaseHolder {

    private static final String TAG = "AppDataBaseHolder";

    private static AppDataBase instance;

    public static AppDataBase getInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(application, AppDataBase.class, "db")
                    .setQueryExecutor(Actions.pool)
                    .setTransactionExecutor(Actions.pool)
                    .build();
        }
        return instance;
    }
}
