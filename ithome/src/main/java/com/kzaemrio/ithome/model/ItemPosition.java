package com.kzaemrio.ithome.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ItemPosition {
    @NonNull
    @PrimaryKey
    private final long mPubDate;

    @ColumnInfo
    private final int mOffset;

    public ItemPosition(@NonNull long pubDate, int offset) {
        mPubDate = pubDate;
        mOffset = offset;
    }

    public long getPubDate() {
        return mPubDate;
    }

    public int getOffset() {
        return mOffset;
    }
}
