package com.kzaemrio.anread.model;

import org.simpleframework.xml.Element;

public class Feed {
    @Element(name = "channel")
    public FeedChannel mFeedChannel;
}
