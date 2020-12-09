package com.kzaemrio.ithome.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kzaemrio.ithome.model.Item;

import java.util.List;

@Dao
public interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Iterable<Item> list);

    @Query("DELETE FROM Item WHERE mPubDate < :time")
    void cleanUp(long time);

    @Query("SELECT * FROM item ORDER BY mPubDate DESC")
    List<Item> getAll();

    @Query("SELECT * FROM item WHERE mLink = :link LIMIT 1")
    LiveData<Item> query(String link);
}
