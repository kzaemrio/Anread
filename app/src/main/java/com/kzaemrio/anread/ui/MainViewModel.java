package com.kzaemrio.anread.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kzaemrio.anread.CacheCleanWorker;
import com.kzaemrio.anread.CacheFeedWorker;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Channel;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";
    private static final String PREF_KEY = "isSyncOn";

    private MutableLiveData<Boolean> mIsSyncOn;
    private MutableLiveData<List<Channel>> mChannelList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        CacheCleanWorker.work(application.getApplicationContext());
    }


    public LiveData<Boolean> getIsSyncOn() {
        if (mIsSyncOn == null) {
            mIsSyncOn = new MutableLiveData<>();
            boolean isSync = gSharedPreferences().getBoolean(PREF_KEY, false);
            CacheFeedWorker.update(getApplication().getApplicationContext(), isSync);
            mIsSyncOn.setValue(isSync);
        }
        return mIsSyncOn;
    }

    public void switchSync() {
        boolean isSync = !Objects.requireNonNull(mIsSyncOn.getValue());
        gSharedPreferences().edit().putBoolean(PREF_KEY, isSync).apply();
        CacheFeedWorker.update(getApplication().getApplicationContext(), isSync);
        mIsSyncOn.setValue(isSync);
    }

    private SharedPreferences gSharedPreferences() {
        Context context = getApplication();
        return context.getSharedPreferences(
                context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE
        );
    }


    public LiveData<List<Channel>> getChannelList() {
        if (mChannelList == null) {
            mChannelList = new MutableLiveData<>();
        }
        return mChannelList;
    }

    public void updateChannelList() {
        ArchTaskExecutor.getInstance().executeOnDiskIO(() -> {
            mChannelList.postValue(AppDatabaseHolder.of(getApplication()).channelDao().getAll());
        });
    }
}
