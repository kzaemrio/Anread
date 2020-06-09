package com.kzaemrio.anread.model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM Item WHERE mLink = (:link)")
    LiveData<Item> query(String link);

    @Query("SELECT * FROM Item WHERE mChannelUrl IN (:channels) ORDER BY mPubDate DESC")
    List<Item> queryBy(String... channels);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Item... subscriptions);

    @Query("DELETE FROM Item WHERE mPubDate < (:time)")
    void deleteBefore(long time);

    @Query("DELETE FROM Item WHERE mChannelUrl = (:channelUrl)")
    void deleteBy(String channelUrl);
}
