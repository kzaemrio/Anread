package com.kzaemrio.anread;

import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.Channel;
import com.kzaemrio.anread.model.Feed;
import com.kzaemrio.anread.model.FeedItem;
import com.kzaemrio.anread.model.Item;

import org.simpleframework.xml.core.Persister;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import androidx.arch.core.executor.ArchTaskExecutor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public interface Actions {
    static void executeOnDiskIO(Runnable runnable) {
        ArchTaskExecutor.getInstance().executeOnDiskIO(runnable);
    }

    static void executeOnMainThread(Runnable runnable) {
        ArchTaskExecutor.getInstance().executeOnMainThread(runnable);
    }

    static RssResult getRssResult(String url) throws Exception {
        Request request = new Request.Builder().url(url).build();
        Response response = new OkHttpClient.Builder().build().newCall(request).execute();
        Feed feed = new Persister().read(Feed.class, Objects.requireNonNull(response.body()).byteStream(), false);
        Channel channel = Channel.create(url, feed.mFeedChannel.mTitle);
        List<Item> list = new LinkedList<>();
        for (FeedItem feedItem : feed.mFeedChannel.mFeedItemList) {
            list.add(Item.create(feedItem, channel.getTitle(), url));
        }
        Item[] itemArray = list.toArray(new Item[0]);
        return RssResult.create(channel, itemArray);
    }

    static void insertRssResult(AppDatabase database, RssResult rssResult) {
        database.channelDao().insert(rssResult.getChannel());
        database.itemDao().insert(rssResult.getItemArray());
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
