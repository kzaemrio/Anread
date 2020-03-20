package com.kzaemrio.anread.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM Item Order by mPubDate DESC Limit 1")
    Item getFirst();

    @Query("SELECT * FROM Item Order by mPubDate DESC")
    List<Item> getAll();

    @Query("SELECT * FROM Item Where mLink = (:link)")
    Item query(String link);

    @Query("SELECT * FROM Item Where mChannelUrl = (:channelUrl) Order by mPubDate DESC")
    List<Item> queryBy(String channelUrl);

    @Query("SELECT * FROM Item Where mPubDate < (:time)")
    Item[] queryBy(long time);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Item... subscriptions);

    @Delete
    void delete(Item... items);
}
