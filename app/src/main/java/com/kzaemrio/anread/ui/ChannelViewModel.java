package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemDao;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ChannelViewModel extends AndroidViewModel {

    private MutableLiveData<List<Item>> mList;
    private MutableLiveData<Boolean> mIsShowLoading;

    public ChannelViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Item>> getList() {
        if (mList == null) {
            mList = new MutableLiveData<>();
        }
        return mList;
    }

    public LiveData<Boolean> getIsShowLoading() {
        if (mIsShowLoading == null) {
            mIsShowLoading = new MutableLiveData<>();
        }
        return mIsShowLoading;
    }

    public void requestList(String url) {
        mIsShowLoading.setValue(true);
        ArchTaskExecutor.getInstance().executeOnDiskIO(() -> {
            try {
                Actions.RssResult result = Actions.getRssResult(url);
                ItemDao itemDao = AppDatabaseHolder.of(getApplication()).itemDao();
                itemDao.insertIgnore(result.getItemArray());
                List<Item> list = itemDao.queryBy(result.getChannel().getUrl());
                mIsShowLoading.postValue(false);
                mList.postValue(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
