package com.kzaemrio.anread.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kzaemrio.anread.CacheCleanWorker;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Channel;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private static final String PREF_KEY = "isSyncOn";

    private final MutableLiveData<Boolean> mIsSyncOn;
    private final LiveData<List<Channel>> mChannelList;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mIsSyncOn = new MutableLiveData<>(sharedPreferences().getBoolean(PREF_KEY, false));

        mChannelList = AppDatabaseHolder.of(application).channelDao().getAll();
    }

    public LiveData<Boolean> getIsSyncOn() {
        return mIsSyncOn;
    }

    public LiveData<List<Channel>> getChannelList() {
        return mChannelList;
    }

    public void switchSync() {
        boolean isSync = !Objects.requireNonNull(mIsSyncOn.getValue());
        sharedPreferences().edit().putBoolean(PREF_KEY, isSync).apply();
        mIsSyncOn.setValue(isSync);
    }

    private SharedPreferences sharedPreferences() {
        Context context = getApplication();
        return context.getSharedPreferences(
                context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE
        );
    }
}
