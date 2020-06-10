package com.kzaemrio.anread.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM Item WHERE mLink = (:link)")
    LiveData<Item> query(String link);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Item... subscriptions);

    @Query("DELETE FROM Item")
    void clear();

    @Query("DELETE FROM Item WHERE mChannelUrl = (:channelUrl)")
    void deleteBy(String channelUrl);
}
