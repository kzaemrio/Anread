package com.kzaemrio.anread.ui;

import android.app.Application;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.adapter.ContentItem;
import com.kzaemrio.anread.adapter.StrId;
import com.kzaemrio.anread.adapter.TimeHeaderItem;
import com.kzaemrio.anread.adapter.TimeItem;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemPosition;
import com.kzaemrio.anread.xml.XMLLexer;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
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
    private MutableLiveData<List<StrId>> mItemList;
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

    public LiveData<List<StrId>> getItemList() {
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
        List<StrId> strIdList = toStrIdList(list);
        mItemList.postValue(strIdList);

        ItemPosition itemPosition = AppDatabaseHolder.of(getApplication()).itemPositionDao().query(mChannelList.toString());
        if (itemPosition != null) {
            for (int i = 0; i < strIdList.size(); i++) {
                StrId item = strIdList.get(i);

                if (item.strId().equals(itemPosition.mItemId)) {
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

    private static List<StrId> toStrIdList(List<Item> itemList) {
        LinkedList<StrId> strIdLinkedList = new LinkedList<>();
        ZoneId zone = ZoneId.systemDefault();
        DateTimeFormatter timeHeaderFormat = DateTimeFormatter.ofPattern("MMM dd EEE");
        DateTimeFormatter timeItemFormat = DateTimeFormatter.ofPattern("HH:mm");
        ZonedDateTime last = null;
        for (Item item : itemList) {
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(item.mPubDate), zone);
            if (last == null || last.getDayOfMonth() - zonedDateTime.getDayOfMonth() > 0) {
                strIdLinkedList.add(new TimeHeaderItem(zonedDateTime.format(timeHeaderFormat)));
            }
            strIdLinkedList.add(new TimeItem(zonedDateTime.format(timeItemFormat), item.mChannelName));
            strIdLinkedList.add(new ContentItem(item.mLink, item.mTitle, parseItemDes(item.mDes)));
            last = zonedDateTime;
        }
        return new ArrayList<>(strIdLinkedList);
    }

    private static String parseItemDes(String des) {
        StringBuilder builder = new StringBuilder();
        TokenSource lexer = new XMLLexer(CharStreams.fromString(des));

        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            if (token.getType() == XMLLexer.TEXT) {
                builder.append(token.getText());
            }
            if (builder.length() > 50) {
                break;
            }
        }
        return builder.toString();
    }

    public void saveItemPosition(int adapterPosition, int offset) {
        String groupId = mChannelList.toString();
        String itemId = Objects.requireNonNull(mItemList.getValue()).get(adapterPosition).strId();

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
