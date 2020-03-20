package com.kzaemrio.anread.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ItemPosition {
    @NonNull
    @PrimaryKey
    public String mId;

    @ColumnInfo
    public String mLink;

    @ColumnInfo
    public int mOffset;

    public static ItemPosition create(String id, String link, int offset) {
        ItemPosition itemPosition = new ItemPosition();
        itemPosition.mId = id;
        itemPosition.mLink = link;
        itemPosition.mOffset = offset;
        return itemPosition;
    }
}
