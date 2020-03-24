package com.kzaemrio.anread.adapter;

public class TimeHeaderItem implements StrId {

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
}
