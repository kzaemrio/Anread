package com.kzaemrio.anread.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ChannelDao {
    @Query("SELECT * FROM Channel ORDER BY mCreateTime")
    List<Channel> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Channel... channels);

    @Delete
    void delete(Channel... channels);
}
