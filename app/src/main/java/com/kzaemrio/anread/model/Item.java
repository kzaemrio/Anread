package com.kzaemrio.anread.model;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

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

    public static Item create(FeedItem feedItem, String channelName, String url) {
        Item item = new Item();
        item.mLink = feedItem.mLink;
        item.mTitle = feedItem.mTitle.trim();
        item.mDes = feedItem.mDes.trim();

        ZonedDateTime originalZonedDateTime = getZonedDateTime(feedItem.mPubDate.trim());
        ZonedDateTime fixedZonedDateTime = originalZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        item.mPubDate = fixedZonedDateTime.toInstant().toEpochMilli();

        item.mChannelName = channelName;
        item.mChannelUrl = url;
        return item;
    }

    private static ZonedDateTime getZonedDateTime(String time) {
        try {
            return ZonedDateTime.parse(time, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            return ZonedDateTime.parse(time, DateTimeFormatter.RFC_1123_DATE_TIME);
        }
    }
}
