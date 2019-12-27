package com.kzaemrio.anread.ui;

import android.app.Application;
import android.content.Context;

import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Channel;
import com.kzaemrio.anread.model.ChannelDao;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemDao;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChannelListViewModel extends AndroidViewModel {

    private MutableLiveData<List<Channel>> mData;

    public ChannelListViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Channel>> getData() {
        if (mData == null) {
            mData = new MutableLiveData<>();
        }
        return mData;
    }

    public void loadChannelList() {
        Observable.<Context>just(getApplication())
                .map(AppDatabaseHolder::of)
                .map(AppDatabase::channelDao)
                .map(ChannelDao::getAll)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(mData::setValue)
                .subscribe();
    }

    public void delete(Channel channel) {
        Observable.just(channel)
                .doOnNext(channelUrl -> {
                    AppDatabase database = AppDatabaseHolder.of(getApplication());
                    ItemDao itemDao = database.itemDao();
                    List<Item> list = itemDao.queryBy(channelUrl.getUrl());
                    itemDao.delete(list.toArray(new Item[0]));
                    database.channelDao().delete(channelUrl);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> loadChannelList())
                .subscribe();
    }
}
