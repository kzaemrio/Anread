package com.kzaemrio.anread.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemPositionDao {
    @Query("SELECT * FROM ItemPosition Where mGroupId = (:id)")
    ItemPosition query(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemPosition itemPosition);

    @Delete
    void delete(ItemPosition itemPosition);
}
