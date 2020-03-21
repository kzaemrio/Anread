package com.kzaemrio.anread;

import android.content.Context;

import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Channel;
import com.kzaemrio.anread.model.ChannelDao;
import com.kzaemrio.anread.model.Item;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CacheFeedWorker extends Worker {

    private static final String NAME = "CACHE_FEED_WORKER";
    private static final String TAG = "CACHE_FEED_WORKER_TAG";

    public CacheFeedWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void update(Context context, boolean isSync) {
        WorkManager manager = WorkManager.getInstance(context);
        if (isSync) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                    CacheFeedWorker.class,
                    1,
                    TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .addTag(CacheFeedWorker.TAG)
                    .build();

            manager.enqueueUniquePeriodicWork(
                    CacheFeedWorker.NAME,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    request
            );
        } else {
            manager.cancelAllWorkByTag(CacheFeedWorker.TAG);
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            AppDatabase database = AppDatabaseHolder.of(getApplicationContext());
            ChannelDao dao = database.channelDao();
            List<Channel> all = dao.getAll();
            List<Item> itemList = new LinkedList<>();
            for (Channel channel : all) {
                Actions.RssResult result = Actions.getRssResult(channel.getUrl());
                Item[] itemArray = result.getItemArray();
                Collections.addAll(itemList, itemArray);
            }
            Item[] items = itemList.toArray(new Item[0]);
            database.itemDao().insert(items);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
