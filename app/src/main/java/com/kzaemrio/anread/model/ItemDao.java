package com.kzaemrio.anread.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM item")
    Observable<List<Item>> getAll();

    @Insert
    void insert(Item... subscriptions);

    @Delete
    void delete(Item... items);
}
