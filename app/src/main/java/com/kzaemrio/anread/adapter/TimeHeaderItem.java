package com.kzaemrio.anread.adapter;

public class TimeHeaderItem implements StrId {

    public static final int TYPE = 0;

    private final String mTime;

    public TimeHeaderItem(String time) {
        mTime = time;
    }

    public String getTime() {
        return mTime;
    }

    @Override
    public String strId() {
        return mTime;
    }

    @Override
    public int type() {
        return TYPE;
    }
}
