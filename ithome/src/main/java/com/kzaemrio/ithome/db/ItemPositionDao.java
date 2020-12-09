package com.kzaemrio.ithome.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kzaemrio.ithome.model.ItemPosition;

@Dao
public interface ItemPositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemPosition itemPosition);

    @Query("DELETE FROM ItemPosition")
    void clear();

    @Query("SELECT * FROM ItemPosition LIMIT 1")
    ItemPosition first();

    default void save(ItemPosition itemPosition) {
        clear();
        insert(itemPosition);
    }
}
