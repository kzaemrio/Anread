package com.kzaemrio.anread.model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ChannelDao {
    @Query("SELECT * FROM Channel ORDER BY mCreateTime")
    LiveData<List<Channel>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Channel channel);

    @Query("Delete From Channel Where mUrl = (:url)")
    void delete(String url);
}
