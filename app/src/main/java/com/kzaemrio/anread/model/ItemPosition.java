package com.kzaemrio.anread.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ItemPosition {
    @NonNull
    @PrimaryKey
    public String mGroupId;

    @ColumnInfo
    public long mPubDate;

    @ColumnInfo
    public int mOffset;

    public static ItemPosition create(String groupId, long pubDate, int offset) {
        ItemPosition it = new ItemPosition();
        it.mGroupId = groupId;
        it.mPubDate = pubDate;
        it.mOffset = offset;
        return it;
    }
}
