package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.adapter.ItemListAdapter;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemPosition;

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
    private final MutableLiveData<List<ItemListAdapter.ViewItem>> mItemList;
    private final MutableLiveData<AdapterItemPosition> mItemPosition;
    private final MutableLiveData<Integer> mNewCount;

    public ItemListViewModel(@NonNull Application application, SavedStateHandle handle) {
        super(application);

        mChannelList = handle.get(KEY_CHANNEL_LIST);

        mIsShowLoading = new MutableLiveData<>();
        mItemList = new MutableLiveData<>();
        mItemPosition = new MutableLiveData<>();
        mNewCount = new MutableLiveData<>();
    }

    public LiveData<Boolean> getIsShowLoading() {
        return mIsShowLoading;
    }

    public LiveData<List<ItemListAdapter.ViewItem>> getItemList() {
        return mItemList;
    }

    public LiveData<AdapterItemPosition> getItemPosition() {
        return mItemPosition;
    }

    public LiveData<Integer> getNewCount() {
        return mNewCount;
    }

    public void updateItemList() {
        Actions.executeOnBackground(() -> {
            mIsShowLoading.postValue(true);
            requestData();
            mIsShowLoading.postValue(false);
        });
    }

    private void requestData() {
        List<ItemListAdapter.ViewItem> list = mChannelList.stream()
                .map(url -> {
                    try {
                        return Actions.getItemArray(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new Item[0];
                    }
                })
                .flatMap(Stream::of)
                .filter(item -> !item.mTitle.contains("之家"))
                .sorted(Comparator.<Item, Long>comparing(i -> i.mPubDate).reversed())
                .map(ItemListAdapter.ViewItem::create)
                .collect(Collectors.toList());

        boolean isInit = mItemList.getValue() == null;

        mItemList.postValue(list);

        if (isInit) {
            ItemPosition itemPosition = AppDatabaseHolder.of(getApplication()).itemPositionDao().query(mChannelList.toString());
            if (itemPosition != null) {
                int index = Actions.binarySearch(list, itemPosition.mPubDate, it -> it.getItem().mPubDate);
                if (index >= 0) {
                    mItemPosition.postValue(AdapterItemPosition.create(
                            index,
                            itemPosition.mOffset
                    ));

                    if (index > 0) {
                        mNewCount.postValue(index);
                    }
                }
            }
        }

        AppDatabaseHolder.of(getApplication()).itemDao().insert(
                list.stream().map(ItemListAdapter.ViewItem::getItem).toArray(Item[]::new)
        );
    }

    public void saveItemPosition(int adapterPosition, int offset) {
        String groupId = mChannelList.toString();
        long pubDate = Objects.requireNonNull(mItemList.getValue()).get(adapterPosition).getItem().mPubDate;

        Actions.executeOnBackground(() -> {
            AppDatabaseHolder.of(getApplication())
                    .itemPositionDao()
                    .insert(ItemPosition.create(groupId, pubDate, offset));
        });
    }

    public void clearItem() {
        Actions.executeOnBackground(() -> AppDatabaseHolder.of(getApplication()).itemDao().clear());
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
