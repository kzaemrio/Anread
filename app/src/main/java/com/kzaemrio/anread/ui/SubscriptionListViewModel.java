package com.kzaemrio.anread.ui;

import android.app.Application;
import android.content.Context;

import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemDao;
import com.kzaemrio.anread.model.Subscription;
import com.kzaemrio.anread.model.SubscriptionDao;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionListViewModel extends AndroidViewModel {

    private MutableLiveData<List<Subscription>> mData;

    public SubscriptionListViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Subscription>> getData() {
        if (mData == null) {
            mData = new MutableLiveData<>();
        }
        return mData;
    }

    public void loadSubscriptionList() {
        Observable.<Context>just(getApplication())
                .map(AppDatabaseHolder::of)
                .map(AppDatabase::subscriptionDao)
                .map(SubscriptionDao::getAll)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(mData::setValue)
                .subscribe();
    }

    public void delete(Subscription subscription) {
        Observable.just(subscription)
                .doOnNext(s -> {
                    AppDatabase database = AppDatabaseHolder.of(getApplication());
                    ItemDao dao = database.itemDao();
                    List<Item> list = dao.queryBy(s.getUrl());
                    dao.delete(list
                    .toArray(new Item[0]));
                    database.subscriptionDao().delete(s);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> loadSubscriptionList())
                .subscribe();
    }
}
