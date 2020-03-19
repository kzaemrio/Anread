package com.kzaemrio.anread.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.CacheCleanWorker;
import com.kzaemrio.anread.CacheFeedWorker;
import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Channel;
import com.kzaemrio.anread.model.Item;

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
    private MutableLiveData<Boolean> mIsShowLoading;
    private MutableLiveData<Boolean> mIsShowAddSubscription;
    private MutableLiveData<List<Item>> mItemList;

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

    public LiveData<Boolean> isShowLoading() {
        if (mIsShowLoading == null) {
            mIsShowLoading = new MutableLiveData<>();
        }
        return mIsShowLoading;
    }

    public LiveData<Boolean> isShowAddSubscription() {
        if (mIsShowAddSubscription == null) {
            mIsShowAddSubscription = new MutableLiveData<>();
        }
        return mIsShowAddSubscription;
    }


    public LiveData<List<Item>> getItemList() {
        if (mItemList == null) {
            mItemList = new MutableLiveData<>();
        }
        return mItemList;
    }

    public void init() {
        mIsShowLoading.setValue(true);
        ArchTaskExecutor.getInstance().executeOnDiskIO(() -> {
            AppDatabase database = AppDatabaseHolder.of(getApplication());
            List<Channel> list = database.channelDao().getAll();

            if (list.isEmpty()) {
                mIsShowAddSubscription.postValue(true);
            } else {
                try {
                    for (Channel channel : list) {
                        String url = channel.getUrl();
                        Actions.RssResult result = Actions.getRssResult(url);
                        Actions.insertRssResult(database, result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mIsShowAddSubscription.postValue(false);
                mItemList.postValue(database.itemDao().getAll());
            }
            mIsShowLoading.postValue(false);
        });
    }
}
