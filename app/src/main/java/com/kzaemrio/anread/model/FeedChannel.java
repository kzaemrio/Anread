package com.kzaemrio.anread.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

public class FeedChannel {
    @Element(name = "title")
    public String mTitle;

    @ElementList(inline = true, entry = "item")
    public List<FeedItem> mFeedItemList;
}
