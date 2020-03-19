package com.kzaemrio.anread.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Channel.class, Item.class, ItemPosition.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ChannelDao channelDao();

    public abstract ItemDao itemDao();

    public abstract ItemPositionDao itemPositionDao();
}
