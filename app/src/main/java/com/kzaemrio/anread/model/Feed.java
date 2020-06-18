package com.kzaemrio.anread.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Feed {
    @Element(name = "channel")
    public FeedChannel mFeedChannel;
}
