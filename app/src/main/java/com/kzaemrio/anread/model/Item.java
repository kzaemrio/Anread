package com.kzaemrio.anread.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @NonNull
    @PrimaryKey
    public String mLink;

    @ColumnInfo
    public String mTitle;

    @ColumnInfo
    public String mDes;

    @ColumnInfo
    public long mPubDate;

    @ColumnInfo
    public String mChannelName;

    @ColumnInfo
    public String mChannelUrl;
}
