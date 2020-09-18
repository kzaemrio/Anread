package com.kzaemrio.ithome;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kzaemrio.ithome.db.AppDataBase;
import com.kzaemrio.ithome.db.AppDataBaseHolder;
import com.kzaemrio.ithome.db.ItemDao;
import com.kzaemrio.ithome.db.ItemPositionDao;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> mIsShowLoading;
    private final MutableLiveData<List<ItemListAdapter.ViewItem>> mItemList;
    private final MutableLiveData<MainView.ScrollPosition> mScrollPosition;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mIsShowLoading = new MutableLiveData<>();
        mItemList = new MutableLiveData<>();
        mScrollPosition = new MutableLiveData<>();
    }

    public LiveData<Boolean> getIsShowLoading() {
        return mIsShowLoading;
    }

    public LiveData<List<ItemListAdapter.ViewItem>> getItemList() {
        return mItemList;
    }

    public LiveData<MainView.ScrollPosition> getScrollPosition() {
        return mScrollPosition;
    }

    public void load() {
        Actions.executeOnBackground(() -> {
            mIsShowLoading.postValue(true);

            AppDataBase db = AppDataBaseHolder.getInstance(getApplication());

            ItemDao dao = db.itemDao();

            dao.insert(Actions.requestItemList());

            dao.cleanUp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2));

            List<ItemListAdapter.ViewItem> list = Actions.mapList(dao.getAll(), ItemListAdapter.ViewItem::create);

            boolean needScrollToLastPosition = mItemList.getValue() == null;

            mItemList.postValue(list);

            if (needScrollToLastPosition) {
                ItemPositionDao itemPositionDao = db.itemPositionDao();
                ItemPosition itemPosition = itemPositionDao.first();
                if (itemPosition != null) {
                    int position = Actions.binarySearch(list, itemPosition.getPubDate(), viewItem -> viewItem.getItem().getPubDate());
                    if (position >= 0) {
                        mScrollPosition.postValue(MainView.ScrollPosition.create(position, itemPosition.getOffset()));
                    }
                    itemPositionDao.delete(itemPosition);
                }
            }

            mIsShowLoading.postValue(false);
        });
    }

    public void saveItemPosition(int position, int offset) {
        Actions.executeOnBackground(() -> {
            AppDataBaseHolder.getInstance(getApplication()).itemPositionDao().insert(
                    new ItemPosition(
                            mItemList.getValue().get(position).getItem().getPubDate(),
                            offset
                    )
            );
        });
    }
}
