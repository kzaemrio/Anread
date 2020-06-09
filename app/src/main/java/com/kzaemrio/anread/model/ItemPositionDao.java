package com.kzaemrio.anread.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemPositionDao {
    @Query("SELECT * FROM ItemPosition Where mGroupId = (:groupId)")
    ItemPosition query(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemPosition itemPosition);

    @Query("DELETE FROM ItemPosition Where mGroupId = (:groupId)")
    void delete(String groupId);
}
