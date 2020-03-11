package com.kzaemrio.anread.model;

import com.kzaemrio.anread.xml.XMLLexer;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
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
        item.mDesItem = parseDesItem(item.mDesDetail);

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

    private static String parseDesItem(String des) {
        StringBuilder builder = new StringBuilder();
        TokenSource lexer = new XMLLexer(CharStreams.fromString(des));

        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            if (token.getType() == XMLLexer.TEXT) {
                builder.append(token.getText());
            }
            if (builder.length() > 50) {
                break;
            }
        }
        return builder.toString();
    }

    private static ZonedDateTime getZonedDateTime(String time) {
        try {
            return ZonedDateTime.parse(time, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            return ZonedDateTime.parse(time, DateTimeFormatter.RFC_1123_DATE_TIME);
        }
    }
}
