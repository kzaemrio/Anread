package com.kzaemrio.anread;

import android.content.Context;

import com.kzaemrio.anread.model.AppDatabaseHolder;

import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CacheCleanWorker extends Worker {

    private static final String NAME = "CACHE_CLEAN_WORK";
    private static final String TAG = "CACHE_CLEAN_WORK_TAG";

    public CacheCleanWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void work(Context context) {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                CacheFeedWorker.class,
                1,
                TimeUnit.DAYS)
                .addTag(CacheCleanWorker.TAG)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                CacheCleanWorker.NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
        );
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDatabaseHolder.of(getApplicationContext()).itemDao().deleteBefore(Instant.now().minus(12, ChronoUnit.HOURS).toEpochMilli());
        return Result.success();
    }
}
