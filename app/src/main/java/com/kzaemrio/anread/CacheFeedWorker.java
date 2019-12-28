package com.kzaemrio.anread;

import android.content.Context;

import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Channel;
import com.kzaemrio.anread.model.ChannelDao;
import com.kzaemrio.anread.model.Item;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.Observable;

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
                    30,
                    TimeUnit.MINUTES)
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
        AppDatabase database = AppDatabaseHolder.of(getApplicationContext());
        Observable.just(database)
                .map(AppDatabase::channelDao)
                .map(ChannelDao::getAll)
                .flatMap(Observable::fromIterable)
                .map(Channel::getUrl)
                .map(Actions::getRssResult)
                .map(Actions.RssResult::getItemArray)
                .flatMap(Observable::fromArray)
                .toList()
                .map(list -> list.toArray(new Item[0]))
                .doOnSuccess(array -> database.itemDao().insertIgnore(array))
                .subscribe();
        return Result.success();
    }
}
