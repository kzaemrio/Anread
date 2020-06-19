package com.kzaemrio.anread;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kzaemrio.anread.model.Channel;
import com.kzaemrio.anread.model.Feed;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.xml.XMLLexer;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public interface Actions {

    ExecutorService pool = Executors.newFixedThreadPool(1);

    OkHttpClient client = new OkHttpClient.Builder().dispatcher(new Dispatcher(pool)).build();

    static void executeOnBackground(Runnable runnable) {
        pool.execute(runnable);
    }

    static Item[] getItemArray(String url) throws Exception {
        InputStream inputStream = getNetworkInputStream(url);
        Feed feed = new Persister().read(Feed.class, inputStream, false);
        Channel channel = Channel.create(url, feed.mFeedChannel.mTitle);
        return feed.mFeedChannel.mFeedItemList.stream()
                .map(feedItem -> Item.create(feedItem, channel.getTitle(), url))
                .toArray(Item[]::new);
    }

    static Channel getChannel(String url) throws IOException {
        InputStream inputStream = getNetworkInputStream(url);
        XMLLexer xmlLexer = new XMLLexer(CharStreams.fromStream(inputStream));

        boolean[] is = {false};
        for (Token token = xmlLexer.nextToken(); token.getType() != Token.EOF; token = xmlLexer.nextToken()) {
            if (is[0]) {
                if (token.getType() == XMLLexer.TEXT) {
                    return Channel.create(url, token.getText());
                }
            } else {
                if (token.getType() == XMLLexer.Name && token.getText().equals("title")) {
                    is[0] = true;
                }
            }
        }

        throw new IllegalArgumentException("error rss url: " + url);
    }

    @NotNull
    static InputStream getNetworkInputStream(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return Objects.requireNonNull(response.body()).byteStream();
    }

    static <T, V extends Comparable<? super V>> int binarySearch(List<T> list, V key, Function<T, V> mapper) {
        return Collections.binarySearch(mapList(list, mapper), key, Comparator.reverseOrder());
    }

    static <T, V> List<V> mapList(List<T> list, Function<T, V> mapper) {
        return new AbstractList<V>() {
            @Override
            public V get(int index) {
                return mapper.apply(list.get(index));
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    static boolean isOnline(Context context) {
        return Optional.ofNullable(context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .map(ConnectivityManager.class::cast)
                .map(ConnectivityManager::getActiveNetworkInfo)
                .map(NetworkInfo::isConnected)
                .orElse(false);
    }
}
