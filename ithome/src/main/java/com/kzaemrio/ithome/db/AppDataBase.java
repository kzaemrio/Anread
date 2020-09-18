package com.kzaemrio.ithome.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kzaemrio.ithome.Item;
import com.kzaemrio.ithome.ItemPosition;

@Database(entities = {Item.class, ItemPosition.class}, exportSchema = false, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ItemDao itemDao();

    public abstract ItemPositionDao itemPositionDao();
}
