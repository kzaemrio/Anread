package com.kzaemrio.anread.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.kzaemrio.anread.adapter.MainAdapter;
import com.kzaemrio.anread.adapter.SimpleDividerItemDecoration;
import com.kzaemrio.anread.adapter.SimpleOffsetItemDecoration;
import com.kzaemrio.anread.databinding.FragmentItemListBinding;
import com.kzaemrio.anread.model.Item;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

public interface ItemListView {
    static ItemListView create(Context context) {
        FragmentItemListBinding binding = FragmentItemListBinding.inflate(LayoutInflater.from(context));

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);

        binding.list.setLayoutManager(layoutManager);
        binding.list.addItemDecoration(new SimpleDividerItemDecoration(context));
        binding.list.addItemDecoration(new SimpleOffsetItemDecoration());

        MainAdapter adapter = new MainAdapter();
        binding.list.setAdapter(adapter);

        return new ItemListView() {
            @Override
            public View getContentView() {
                return binding.getRoot();
            }

            @Override
            public void showLoading(boolean isShow) {
                binding.swipe.setRefreshing(isShow);
            }

            @Override
            public void bind(List<Item> list) {
                adapter.submitList(list);
            }

            @Override
            public void setCallback(Callback callback) {
                binding.swipe.setOnRefreshListener(callback::onRefresh);
                adapter.setItemConsumer(callback::onClick);
            }

            @Override
            public void scrollTo(ItemListViewModel.AdapterItemPosition adapterItemPosition) {
                layoutManager.scrollToPositionWithOffset(adapterItemPosition.position(), adapterItemPosition.offset());
            }

            @Override
            public int getAdapterPosition() {
                return binding.list.getChildAdapterPosition(binding.list.getChildAt(0));
            }

            @Override
            public int getOffset() {
                return binding.list.getChildAt(0).getTop();
            }
        };
    }

    View getContentView();

    void showLoading(boolean isShow);

    void bind(List<Item> list);

    void setCallback(Callback callback);

    void scrollTo(ItemListViewModel.AdapterItemPosition adapterItemPosition);

    int getAdapterPosition();

    int getOffset();

    interface Callback {
        void onRefresh();

        void onClick(Item item);
    }
}
