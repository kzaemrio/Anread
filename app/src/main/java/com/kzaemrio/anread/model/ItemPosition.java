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
    public String mItemId;

    @ColumnInfo
    public int mOffset;

    public static ItemPosition create(String groupId, String itemId, int offset) {
        ItemPosition itemPosition = new ItemPosition();
        itemPosition.mGroupId = groupId;
        itemPosition.mItemId = itemId;
        itemPosition.mOffset = offset;
        return itemPosition;
    }
}
