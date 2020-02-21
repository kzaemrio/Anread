package com.kzaemrio.anread.model;

import android.text.Html;

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
    public String mDesDetail;

    @ColumnInfo
    public String mDesItem;

    @ColumnInfo
    public long mPubDate;

    @ColumnInfo
    public String mPubDateItem;

    @ColumnInfo
    public String mPubDateDetail;

    @ColumnInfo
    public String mChannelName;

    @ColumnInfo
    public String mChannelUrl;

    @ColumnInfo
    public int mIsRead;

    public static Item create(FeedItem feedItem, String channelName, String url) {
        Item item = new Item();
        item.mLink = feedItem.mLink;
        item.mTitle = feedItem.mTitle.trim();

        item.mDesDetail = feedItem.mDes.trim();
        item.mDesItem = Html.fromHtml(getDes(item.mDesDetail)).toString();

        ZonedDateTime originalZonedDateTime = getZonedDateTime(feedItem.mPubDate.trim());
        ZonedDateTime fixedZonedDateTime = originalZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        item.mPubDate = fixedZonedDateTime.toInstant().toEpochMilli();
        item.mPubDateItem = fixedZonedDateTime.format(DateTimeFormatter.ofPattern("EEE dd"));
        item.mPubDateDetail = fixedZonedDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));

        item.mChannelName = channelName;
        item.mChannelUrl = url;
        item.mIsRead = 0;
        return item;
    }

    private static String getDes(String des) {
        String start = "<p>";
        String end = "</p>";
        int startIndex = des.indexOf(start);
        int endIndex = des.indexOf(end);

        if (startIndex >= 0 && endIndex > startIndex) {
            String substring = des.substring(startIndex + start.length(), endIndex);
            if (Html.fromHtml(substring).length() < 60) {
                return substring + "\n" + getDes(des.substring(endIndex + end.length()));
            } else {
                return substring;
            }
        } else {
            return des;
        }
    }

    private static ZonedDateTime getZonedDateTime(String time) {
        try {
            return ZonedDateTime.parse(time, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            return ZonedDateTime.parse(time, DateTimeFormatter.RFC_1123_DATE_TIME);
        }
    }
}
