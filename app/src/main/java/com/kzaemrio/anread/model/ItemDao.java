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

    @Query("SELECT * FROM item Where mLink = (:link)")
    Item query(String link);

    @Query("SELECT * FROM item Where mChannelUrl = (:link)")
    List<Item> queryBy(String link);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReplace(Item... subscriptions);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIgnore(Item... subscriptions);

    @Delete
    void delete(Item... items);

    @Query("SELECT COUNT(mIsRead) FROM item Where mIsRead = 0")
    int countUnRead();

    @Query("SELECT COUNT(mChannelUrl) FROM item Where mChannelUrl = (:url)")
    int countUnRead(String url);

    @Query("SELECT COUNT(mIsFav) FROM item Where mIsFav = 1")
    int countFav();
}
