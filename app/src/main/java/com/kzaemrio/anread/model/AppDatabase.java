package com.kzaemrio.anread.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Channel.class, Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ChannelDao channelDao();

    public abstract ItemDao itemDao();
}
