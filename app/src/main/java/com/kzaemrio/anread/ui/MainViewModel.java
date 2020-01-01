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
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        Observable.<Context>just(getApplication())
                .doOnNext(context -> {
                    AppDatabase database = AppDatabaseHolder.of(context);
                    List<Channel> list = database.channelDao().getAll();

                    if (list.isEmpty()) {
                        mIsShowAddSubscription.postValue(true);
                    } else {
                        Observable.fromIterable(list)
                                .map(Channel::getUrl)
                                .map(Actions::getRssResult)
                                .doOnNext(rssResult -> Actions.insertRssResult(database, rssResult))
                                .subscribe();

                        mIsShowAddSubscription.postValue(false);
                        mItemList.postValue(database.itemDao().getAll());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> mIsShowLoading.postValue(true))
                .doOnComplete(() -> mIsShowLoading.postValue(false))
                .subscribe();
    }

    public void readAll() {
        if (mItemList != null && mItemList.getValue() != null) {
            Observable.fromIterable(mItemList.getValue())
                    .doOnNext(item -> item.mIsRead = 1)
                    .toList()
                    .doOnSuccess(list -> {
                        mItemList.postValue(list);
                        AppDatabaseHolder.of(getApplication()).itemDao().insertReplace(list.toArray(new Item[0]));
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

                    .subscribe();
        }
    }
}
