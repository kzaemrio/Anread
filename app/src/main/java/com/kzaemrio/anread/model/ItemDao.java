package com.kzaemrio.anread.model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM Item Where mLink = (:link)")
    LiveData<Item> query(String link);

    @Query("SELECT * FROM Item Where mChannelUrl in (:channels) Order by mPubDate DESC")
    List<Item> queryBy(String... channels);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Item... subscriptions);

    @Query("delete from Item Where mPubDate < (:time)")
    void deleteBefore(long time);

    @Query("DELETE FROM Item Where mChannelUrl = (:channelUrl)")
    void deleteBy(String channelUrl);
}
