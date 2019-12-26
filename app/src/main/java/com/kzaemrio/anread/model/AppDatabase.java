package com.kzaemrio.anread.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Subscription.class, Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SubscriptionDao subscriptionDao();

    public abstract ItemDao itemDao();
}
