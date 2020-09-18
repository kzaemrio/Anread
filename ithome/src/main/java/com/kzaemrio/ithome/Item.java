package com.kzaemrio.ithome;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @ColumnInfo
    private final String mTitle;

    @NonNull
    @PrimaryKey
    private final String mLink;

    @ColumnInfo
    private final String mDes;

    @ColumnInfo
    private final long mPubDate;

    @ColumnInfo
    private final String mChannelUrl;

    @ColumnInfo
    private final String mChannelName;

    public Item(String title, @NonNull String link, String des, long pubDate, String channelUrl, String channelName) {
        mTitle = title;
        mLink = link;
        mDes = des;
        mPubDate = pubDate;
        mChannelUrl = channelUrl;
        mChannelName = channelName;
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
            return new Item(
                    mTitle,
                    mLink,
                    mDes,
                    mPubDate,
                    mChannelUrl,
                    mChannelName
            );
        }
    }
}
