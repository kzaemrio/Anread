package com.kzaemrio.anread.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemPositionDao {
    @Query("SELECT * FROM ItemPosition Where mId = (:id)")
    ItemPosition query(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReplace(ItemPosition... itemPositions);
}
