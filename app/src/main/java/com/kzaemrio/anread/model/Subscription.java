package com.kzaemrio.anread.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Subscription {
    @NonNull
    @PrimaryKey
    String mUrl;

    @ColumnInfo
    String mTitle;

    @ColumnInfo
    public long mCreateTime;

    public static Subscription create(String url, String title) {
        Subscription channel = new Subscription();
        channel.mUrl = url;
        channel.mTitle = title;
        channel.mCreateTime = System.currentTimeMillis();
        return channel;
    }
}
