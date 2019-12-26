package com.kzaemrio.anread.model;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseHolder {

    private static AppDatabase database;

    public static final AppDatabase of(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "anread-db"
            ).build();
        }
        return database;
    }
}
