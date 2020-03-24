package com.kzaemrio.anread.adapter;

public class TimeItem implements StrId {

    private final String mTime;
    private final String mChannelName;

    public TimeItem(String time, String channelName) {
        mTime = time;
        mChannelName = channelName;
    }

    public String getTime() {
        return mTime;
    }

    public String getChannelName() {
        return mChannelName;
    }

    @Override
    public String strId() {
        return mTime;
    }
}
