package com.kzaemrio.ithome.event;

public class OnScrollToPositionWithOffsetEvent {

    private final int mIndex;
    private final int mOffset;

    public OnScrollToPositionWithOffsetEvent(int index, int offset) {
        mIndex = index;
        mOffset = offset;
    }

    public int getIndex() {
        return mIndex;
    }

    public int getOffset() {
        return mOffset;
    }
}
