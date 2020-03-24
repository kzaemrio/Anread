package com.kzaemrio.anread.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.drakeet.multitype.MultiTypeAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.kzaemrio.anread.R;
import com.kzaemrio.anread.adapter.ContentItem;
import com.kzaemrio.anread.adapter.ContentItemBinder;
import com.kzaemrio.anread.adapter.SimpleOffsetItemDecoration;
import com.kzaemrio.anread.adapter.StrId;
import com.kzaemrio.anread.adapter.TimeHeaderItem;
import com.kzaemrio.anread.adapter.TimeHeaderItemBinder;
import com.kzaemrio.anread.adapter.TimeItem;
import com.kzaemrio.anread.adapter.TimeItemBinder;
import com.kzaemrio.anread.databinding.FragmentItemListBinding;
import com.kzaemrio.anread.model.Item;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

public interface ItemListView {
    static ItemListView create(Context context) {
        FragmentItemListBinding binding = FragmentItemListBinding.inflate(LayoutInflater.from(context));

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);

        binding.list.setLayoutManager(layoutManager);

        MultiTypeAdapter adapter = new MultiTypeAdapter();
        adapter.register(TimeHeaderItem.class, new TimeHeaderItemBinder());
        adapter.register(TimeItem.class, new TimeItemBinder());
        adapter.register(ContentItem.class, new ContentItemBinder());
        binding.list.setAdapter(adapter);

        binding.list.addItemDecoration(new SimpleOffsetItemDecoration());

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
            public void bind(List<StrId> list) {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() {
                        return adapter.getItemCount();
                    }

                    @Override
                    public int getNewListSize() {
                        return list.size();
                    }

                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        return ((StrId) adapter.getItems().get(oldItemPosition)).strId().equals(list.get(newItemPosition).strId());
                    }

                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        return true;
                    }
                });
                adapter.setItems(list);
                diffResult.dispatchUpdatesTo(adapter);
            }

            @Override
            public void setCallback(Callback callback) {
                binding.swipe.setOnRefreshListener(callback::onRefresh);
                adapter.register(ContentItem.class, new ContentItemBinder(callback::onClick));
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

            @Override
            public void alertHasNew(int count) {
                Snackbar snackbar = Snackbar.make(
                        binding.getRoot(),
                        context.getString(R.string.alert_has_new, count),
                        Snackbar.LENGTH_SHORT
                );
                View view = snackbar.getView();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                view.setLayoutParams(layoutParams);
                snackbar.show();
            }
        };
    }

    View getContentView();

    void showLoading(boolean isShow);

    void bind(List<StrId> list);

    void setCallback(Callback callback);

    void scrollTo(ItemListViewModel.AdapterItemPosition adapterItemPosition);

    int getAdapterPosition();

    int getOffset();

    void alertHasNew(int count);

    interface Callback {
        void onRefresh();

        void onClick(String itemLink);
    }
}
