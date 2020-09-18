package com.kzaemrio.ithome.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kzaemrio.ithome.ItemPosition;

@Dao
public interface ItemPositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemPosition itemPosition);

    @Delete
    void delete(ItemPosition itemPosition);

    @Query("SELECT * FROM ItemPosition LIMIT 1")
    ItemPosition first();
}
