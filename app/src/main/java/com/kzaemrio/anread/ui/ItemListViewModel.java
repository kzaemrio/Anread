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
import androidx.lifecycle.SavedStateHandle;

public class ItemListViewModel extends AndroidViewModel {

    public static final String KEY_CHANNEL_LIST = "KEY_CHANNEL_LIST";

    private final List<String> mChannelList;

    private final MutableLiveData<Boolean> mIsShowLoading;
    private final MutableLiveData<Integer> mHasNew;
    private final MutableLiveData<List<ItemListAdapter.ViewItem>> mItemList;
    private final MutableLiveData<AdapterItemPosition> mItemPosition;

    public ItemListViewModel(@NonNull Application application, SavedStateHandle handle) {
        super(application);

        mChannelList = handle.get(KEY_CHANNEL_LIST);

        mIsShowLoading = new MutableLiveData<>();
        mHasNew = new MutableLiveData<>();
        mItemList = new MutableLiveData<>();
        mItemPosition = new MutableLiveData<>();
    }

    public LiveData<Boolean> getIsShowLoading() {
        return mIsShowLoading;
    }

    public LiveData<Integer> getHasNew() {
        return mHasNew;
    }

    public LiveData<List<ItemListAdapter.ViewItem>> getItemList() {
        return mItemList;
    }

    public LiveData<AdapterItemPosition> getItemPosition() {
        return mItemPosition;
    }

    public void updateItemList() {
        Actions.executeOnDiskIO(() -> {
            mIsShowLoading.postValue(true);
            loadOnLine(loadCache());
            mIsShowLoading.postValue(false);
        });
    }

    private List<ItemListAdapter.ViewItem> loadCache() {
        List<ItemListAdapter.ViewItem> list = AppDatabaseHolder.of(getApplication())
                .itemDao()
                .queryBy(mChannelList.toArray(new String[0]))
                .stream()
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

        return list;
    }

    private void loadOnLine(List<ItemListAdapter.ViewItem> cacheList) {
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
                .filter(item -> item.mPubDate > (cacheList.size() > 0 ? cacheList.get(0).getItem().mPubDate : 0))
                .sorted(Comparator.<Item, Long>comparing(i -> i.mPubDate).reversed())
                .map(ItemListAdapter.ViewItem::create)
                .collect(Collectors.toList());

        ArrayList<ItemListAdapter.ViewItem> result = new ArrayList<>(newList.size() + cacheList.size());
        result.addAll(newList);
        result.addAll(cacheList);
        mItemList.postValue(result);

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
