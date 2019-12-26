package com.kzaemrio.anread.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM item Order by mPubDate")
    List<Item> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Item... subscriptions);

    @Delete
    void delete(Item... items);

    @Query("SELECT COUNT(mIsRead) FROM item Where mIsRead = 0")
    int countUnRead();

    @Query("SELECT COUNT(mChannelUrl) FROM item Where mChannelUrl = (:url)")
    int countUnRead(String url);

    @Query("SELECT COUNT(mIsFav) FROM item Where mIsFav = 1")
    int countFav();
}
