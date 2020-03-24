package com.kzaemrio.anread.adapter;

public class ContentItem implements StrId {

    private final String mLink;
    private final String mTitle;
    private final String mDes;

    public ContentItem(String link, String title, String des) {
        mLink = link;
        mTitle = title;
        mDes = des;
    }

    public String getLink() {
        return mLink;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDes() {
        return mDes;
    }

    @Override
    public String strId() {
        return mLink;
    }
}
