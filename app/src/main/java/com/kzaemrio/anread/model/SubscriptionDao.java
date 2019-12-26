package com.kzaemrio.anread.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

@Dao
public interface SubscriptionDao {
    @Query("SELECT * FROM subscription")
    Observable<List<Subscription>> getAll();

    @Insert
    void insert(Subscription... subscriptions);

    @Delete
    void delete(Subscription... subscriptions);
}
