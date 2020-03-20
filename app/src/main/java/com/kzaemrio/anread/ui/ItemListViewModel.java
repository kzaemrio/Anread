package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemDao;
import com.kzaemrio.anread.model.ItemPosition;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ItemListViewModel extends AndroidViewModel {

    private List<String> mChannelList;

    private MutableLiveData<Boolean> mIsShowLoading;
    private MutableLiveData<Boolean> mHasNew;
    private MutableLiveData<List<Item>> mItemList;
    private MutableLiveData<AdapterItemPosition> mItemPosition;

    public ItemListViewModel(@NonNull Application application) {
        super(application);
    }

    public void setChannelList(List<String> channelList) {
        mChannelList = channelList;
    }

    public LiveData<Boolean> getIsShowLoading() {
        if (mIsShowLoading == null) {
            mIsShowLoading = new MutableLiveData<>();
        }
        return mIsShowLoading;
    }

    public LiveData<Boolean> getHasNew() {
        if (mHasNew == null) {
            mHasNew = new MutableLiveData<>();
        }
        return mHasNew;
    }

    public LiveData<List<Item>> getItemList() {
        if (mItemList == null) {
            mItemList = new MutableLiveData<>();
        }
        return mItemList;
    }

    public LiveData<AdapterItemPosition> getItemPosition() {
        if (mItemPosition == null) {
            mItemPosition = new MutableLiveData<>();
        }
        return mItemPosition;
    }

    public void updateItemList() {
        Actions.executeOnDiskIO(() -> {
            loadCache();
            loadOnline();
        });
    }

    private void loadOnline() {
        try {
            mIsShowLoading.postValue(true);
            AppDatabase db = AppDatabaseHolder.of(getApplication());
            ItemDao itemDao = db.itemDao();
            Item firstOld = itemDao.getFirst();

            for (String channel : mChannelList) {
                Actions.RssResult result = Actions.getRssResult(channel);
                Actions.insertRssResult(db, result);
            }
            mItemList.postValue(itemDao.getAll());
            Item firstNew = itemDao.getFirst();
            mHasNew.postValue(!Objects.equals(firstOld.mLink, firstNew.mLink));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mIsShowLoading.postValue(false);
        }
    }

    private void loadCache() {
        AppDatabase db = AppDatabaseHolder.of(getApplication());
        List<Item> list = db.itemDao().getAll();
        if (list != null && list.size() > 0) {
            mItemList.postValue(list);

            ItemPosition itemPosition = db.itemPositionDao().query(mChannelList.toString());
            if (itemPosition != null) {
                for (int i = 0; i < list.size(); i++) {
                    Item item = list.get(i);

                    if (item.mLink.equals(itemPosition.mLink)) {
                        mItemPosition.postValue(AdapterItemPosition.create(
                                i,
                                itemPosition.mOffset
                        ));
                        break;
                    }
                }
            }
        }
    }

    public void saveItemPosition(int adapterPosition, int offset) {
        String id = mChannelList.toString();
        String link = Objects.requireNonNull(mItemList.getValue()).get(adapterPosition).mLink;

        Actions.executeOnDiskIO(() -> {
            AppDatabaseHolder.of(getApplication())
                    .itemPositionDao()
                    .insert(ItemPosition.create(id, link, offset));
        });
    }

    public interface AdapterItemPosition {
        static AdapterItemPosition create(int position, int offset) {
            return new AdapterItemPosition() {
                @Override
                public int position() {
                    return position;
                }

                @Override
                public int offset() {
                    return offset;
                }
            };
        }

        int position();

        int offset();
    }
}
