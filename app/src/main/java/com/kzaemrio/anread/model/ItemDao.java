package com.kzaemrio.anread.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM item Order by mPubDate DESC")
    List<Item> getAll();

    @Query("SELECT * FROM item Where mLink = (:link)")
    Item query(String link);

    @Query("SELECT * FROM item Where mChannelUrl = (:channelUrl) Order by mPubDate DESC")
    List<Item> queryBy(String channelUrl);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReplace(Item... subscriptions);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIgnore(Item... subscriptions);

    @Delete
    void delete(Item... items);
}
