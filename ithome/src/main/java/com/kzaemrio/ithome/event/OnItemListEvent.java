package com.kzaemrio.ithome.event;

import com.kzaemrio.ithome.ItemListAdapter;

import java.util.List;

public class OnItemListEvent {

    private final List<ItemListAdapter.ViewItem> mList;

    public OnItemListEvent(List<ItemListAdapter.ViewItem> list) {
        mList = list;
    }

    public List<ItemListAdapter.ViewItem> getList() {
        return mList;
    }
}
