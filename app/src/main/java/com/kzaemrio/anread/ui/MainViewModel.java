package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.model.AppDatabaseHolder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private MutableLiveData<Boolean> mIsShowLoading;
    private MutableLiveData<Boolean> mIsShowAddSubscription;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> isShowLoading() {
        if (mIsShowLoading == null) {
            mIsShowLoading = new MutableLiveData<>();
            mIsShowLoading.setValue(false);
        }
        return mIsShowLoading;
    }

    public LiveData<Boolean> isShowAddSubscription() {
        if (mIsShowAddSubscription == null) {
            mIsShowAddSubscription = new MutableLiveData<>();
            mIsShowAddSubscription.setValue(false);
        }
        return mIsShowAddSubscription;
    }

    public void init() {
        mIsShowLoading.setValue(true);

        AppDatabaseHolder.of(getApplication())
                .subscriptionDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(list -> {
                    if (list.isEmpty()) {
                        mIsShowLoading.setValue(false);
                        mIsShowAddSubscription.setValue(true);
                    }
                })
                .subscribe();
    }
}
