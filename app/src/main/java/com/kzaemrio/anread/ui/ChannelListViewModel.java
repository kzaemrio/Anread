package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Channel;
import com.kzaemrio.anread.model.ChannelDao;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemDao;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
        ArchTaskExecutor.getInstance().executeOnDiskIO(() -> {
            mData.postValue(AppDatabaseHolder.of(getApplication()).channelDao().getAll());
        });
    }

    public void delete(Channel channel) {
        ArchTaskExecutor.getInstance().executeOnDiskIO(() -> {
            AppDatabase appDatabase = AppDatabaseHolder.of(getApplication());

            ItemDao itemDao = appDatabase.itemDao();
            List<Item> list = itemDao.queryBy(channel.getUrl());
            itemDao.delete(list.toArray(new Item[0]));

            ChannelDao channelDao = appDatabase.channelDao();
            channelDao.delete(channel);
            mData.postValue(channelDao.getAll());
        });
    }
}
