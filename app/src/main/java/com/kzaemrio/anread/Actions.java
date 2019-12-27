package com.kzaemrio.anread;

import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.Channel;
import com.kzaemrio.anread.model.Feed;
import com.kzaemrio.anread.model.Item;

import org.simpleframework.xml.core.Persister;

import java.util.Objects;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public interface Actions {

    static RssResult getRssResult(String url) throws Exception {
        Request request = new Request.Builder().url(url).build();
        Response response = new OkHttpClient.Builder().build().newCall(request).execute();
        Feed feed = new Persister().read(Feed.class, Objects.requireNonNull(response.body()).byteStream(), false);
        Channel channel = Channel.create(url, feed.mFeedChannel.mTitle);
        Item[] itemList = Observable.fromIterable(feed.mFeedChannel.mFeedItemList)
                .map(feedItem -> Item.create(feedItem, channel.getTitle(), url))
                .toList()
                .map(list -> list.toArray(new Item[0]))
                .blockingGet();
        return RssResult.create(channel, itemList);
    }

    static void insertRssResult(AppDatabase database, RssResult rssResult) {
        database.channelDao().insert(rssResult.getChannel());
        database.itemDao().insertIgnore(rssResult.getItemArray());
    }

    interface RssResult {
        static RssResult create(Channel channel, Item[] itemList) {
            return new RssResult() {
                @Override
                public Channel getChannel() {
                    return channel;
                }

                @Override
                public Item[] getItemArray() {
                    return itemList;
                }
            };
        }

        Channel getChannel();

        Item[] getItemArray();
    }
}
