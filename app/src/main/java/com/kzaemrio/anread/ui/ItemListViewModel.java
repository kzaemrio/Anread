package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.adapter.ItemListAdapter;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemPosition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ItemListViewModel extends AndroidViewModel {

    private List<String> mChannelList;

    private MutableLiveData<Boolean> mIsShowLoading;
    private MutableLiveData<Integer> mHasNew;
    private MutableLiveData<List<ItemListAdapter.ViewItem>> mItemList;
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

    public LiveData<Integer> getHasNew() {
        if (mHasNew == null) {
            mHasNew = new MutableLiveData<>();
        }
        return mHasNew;
    }

    public LiveData<List<ItemListAdapter.ViewItem>> getItemList() {
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
            loadOnLine();
        });
    }

    private void loadCache() {
        List<ItemListAdapter.ViewItem> list = mChannelList.stream()
                .map(AppDatabaseHolder.of(getApplication()).itemDao()::queryBy)
                .flatMap(List::stream)
                .sorted(Comparator.<Item, Long>comparing(i -> i.mPubDate).reversed())
                .map(ItemListAdapter.ViewItem::create)
                .collect(Collectors.toList());

        mItemList.postValue(list);

        ItemPosition itemPosition = AppDatabaseHolder.of(getApplication()).itemPositionDao().query(mChannelList.toString());
        if (itemPosition != null) {
            for (int i = 0; i < list.size(); i++) {
                ItemListAdapter.ViewItem item = list.get(i);

                if (item.getItem().mLink.equals(itemPosition.mItemId)) {
                    mItemPosition.postValue(AdapterItemPosition.create(
                            i,
                            itemPosition.mOffset
                    ));
                    break;
                }
            }
        }
    }

    private void loadOnLine() {
        mIsShowLoading.postValue(true);
        List<ItemListAdapter.ViewItem> cacheList = Objects.requireNonNull(mItemList.getValue());

        List<ItemListAdapter.ViewItem> newList = mChannelList.stream()
                .map(url -> {
                    try {
                        return Actions.getItemArray(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new Item[0];
                    }
                })
                .flatMap(Stream::of)
                .filter(item -> item.mPubDate > cacheList.get(0).getItem().mPubDate)
                .sorted(Comparator.<Item, Long>comparing(i -> i.mPubDate).reversed())
                .map(ItemListAdapter.ViewItem::create)
                .collect(Collectors.toList());

        ArrayList<ItemListAdapter.ViewItem> result = new ArrayList<>(newList.size() + cacheList.size());
        result.addAll(newList);
        result.addAll(cacheList);
        mItemList.postValue(result);

        mIsShowLoading.postValue(false);

        if (newList.size() > 0) {
            mHasNew.postValue(newList.size());
            AppDatabaseHolder.of(getApplication()).itemDao().insert(
                    newList.stream().map(ItemListAdapter.ViewItem::getItem).toArray(Item[]::new)
            );
        }
    }

    public void saveItemPosition(int adapterPosition, int offset) {
        String groupId = mChannelList.toString();
        String itemId = Objects.requireNonNull(mItemList.getValue()).get(adapterPosition).getItem().mLink;

        Actions.executeOnDiskIO(() -> {
            AppDatabaseHolder.of(getApplication())
                    .itemPositionDao()
                    .insert(ItemPosition.create(groupId, itemId, offset));
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
