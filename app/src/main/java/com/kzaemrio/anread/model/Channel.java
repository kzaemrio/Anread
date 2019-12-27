package com.kzaemrio.anread.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Channel {
    @NonNull
    @PrimaryKey
    private String mUrl;

    @ColumnInfo
    private String mTitle;

    @ColumnInfo
    private long mCreateTime;

    @NonNull
    public String getUrl() {
        return mUrl;
    }

    public void setUrl(@NonNull String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long createTime) {
        mCreateTime = createTime;
    }

    public static Channel create(String url, String title) {
        Channel channel = new Channel();
        channel.mUrl = url;
        channel.mTitle = title;
        channel.mCreateTime = System.currentTimeMillis();
        return channel;
    }
}