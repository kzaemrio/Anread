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
    public String mPubDate;

    @ColumnInfo
    public String mChannelName;

    @ColumnInfo
    public String mChannelUrl;

    @ColumnInfo
    public int mIsRead;

    @ColumnInfo
    public int mIsFav;

    public static Item create(FeedItem feedItem, String channelName, String url) {
        Item item = new Item();
        item.mLink = feedItem.mLink;
        item.mTitle = feedItem.mTitle.trim();
        item.mDes = feedItem.mDes.trim();
        item.mPubDate = feedItem.mPubDate.trim();
        item.mChannelName = channelName;
        item.mChannelUrl = url;
        item.mIsRead = 0;
        item.mIsFav = 0;
        return item;
    }
}
