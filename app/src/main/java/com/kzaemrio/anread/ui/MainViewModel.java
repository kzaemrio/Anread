package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Channel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {

    private final LiveData<List<Channel>> mChannelList;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mChannelList = AppDatabaseHolder.of(application).channelDao().getAll();
    }

    public LiveData<List<Channel>> getChannelList() {
        return mChannelList;
    }
}
