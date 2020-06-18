package com.kzaemrio.anread.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class FeedItem {
    @Element(name = "title")
    public String mTitle;

    @Element(name = "link")
    public String mLink;

    @Element(name = "description")
    public String mDes;

    @Element(name = "pubDate")
    public String mPubDate;
}
