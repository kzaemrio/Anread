package com.kzaemrio.ithome;

public class Item {
    private final String mTitle;
    private final String mLink;
    private final String mDes;
    private final long mPubDate;

    private final String mChannelUrl;
    private final String mChannelName;

    private Item(Builder builder) {
        mTitle = builder.mTitle;
        mDes = builder.mDes;
        mPubDate = builder.mPubDate;
        mChannelName = builder.mChannelName;
        mLink = builder.mLink;
        mChannelUrl = builder.mChannelUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public String getDes() {
        return mDes;
    }

    public long getPubDate() {
        return mPubDate;
    }

    public String getChannelUrl() {
        return mChannelUrl;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public static final class Builder {

        private String mTitle;
        private String mLink;
        private String mDes;
        private long mPubDate;
        private String mChannelUrl;
        private String mChannelName;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withTitle(String mTitle) {
            this.mTitle = mTitle;
            return this;
        }

        public Builder withLink(String mLink) {
            this.mLink = mLink;
            return this;
        }

        public Builder withDes(String mDes) {
            this.mDes = mDes;
            return this;
        }

        public Builder withPubDate(long mPubDate) {
            this.mPubDate = mPubDate;
            return this;
        }

        public Builder withChannelUrl(String mChannelUrl) {
            this.mChannelUrl = mChannelUrl;
            return this;
        }

        public Builder withChannelName(String mChannelName) {
            this.mChannelName = mChannelName;
            return this;
        }

        public boolean isSkip() {
            return mTitle.contains("IT之家");
        }

        public Item build() {
            return new Item(this);
        }
    }
}
