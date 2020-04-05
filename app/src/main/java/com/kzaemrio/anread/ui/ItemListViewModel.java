package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.adapter.ItemListAdapter;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemPosition;

import java.util.Arrays;
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
        Actions.executeOnDiskIO(this::loadOnLine);
    }

    private void loadOnLine() {
        mIsShowLoading.postValue(true);
        List<Item> list = mChannelList.stream()
                .map(url -> {
                    try {
                        return Actions.getItemArray(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new Item[0];
                    }
                })
                .peek(array -> Arrays.sort(array, Comparator.<Item, Long>comparing(i -> i.mPubDate).reversed()))
                .flatMap(Stream::of)
                .sorted(Comparator.<Item, Long>comparing(i -> i.mPubDate).reversed())
                .collect(Collectors.toList());
        List<ItemListAdapter.ViewItem> itemList = toStrIdList(list);
        mItemList.postValue(itemList);

        ItemPosition itemPosition = AppDatabaseHolder.of(getApplication()).itemPositionDao().query(mChannelList.toString());
        if (itemPosition != null) {
            for (int i = 0; i < itemList.size(); i++) {
                ItemListAdapter.ViewItem item = itemList.get(i);

                if (item.getItem().mLink.equals(itemPosition.mItemId)) {
                    mItemPosition.postValue(AdapterItemPosition.create(
                            i,
                            itemPosition.mOffset
                    ));
                    break;
                }
            }
        }
        mIsShowLoading.postValue(false);

        AppDatabaseHolder.of(getApplication()).itemDao().insert(list.toArray(new Item[0]));
    }

    private static List<ItemListAdapter.ViewItem> toStrIdList(List<Item> itemList) {
        return itemList.stream()
                .map(ItemListAdapter.ViewItem::create)
                .collect(Collectors.toList());
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
