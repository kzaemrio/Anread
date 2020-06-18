package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Channel;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ChannelListViewModel extends AndroidViewModel {

    private final LiveData<List<Channel>> mChannelList;

    public ChannelListViewModel(@NonNull Application application) {
        super(application);

        mChannelList = AppDatabaseHolder.of(getApplication()).channelDao().getAll();
    }

    public LiveData<List<Channel>> getChannelList() {
        return mChannelList;
    }

    public void delete(Channel channel) {
        Actions.executeOnBackground(() -> {
            AppDatabase appDatabase = AppDatabaseHolder.of(getApplication());

            appDatabase.itemDao().deleteBy(channel.getUrl());

            appDatabase.itemPositionDao().delete(Collections.singleton(channel.getUrl()).toString());

            appDatabase.channelDao().delete(channel.getUrl());
        });
    }
}
