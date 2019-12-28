package com.kzaemrio.anread.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.kzaemrio.anread.R;
import com.kzaemrio.anread.adapter.AddSubscriptionAdapter;
import com.kzaemrio.anread.adapter.MainAdapter;
import com.kzaemrio.anread.adapter.SimpleDividerItemDecoration;
import com.kzaemrio.anread.adapter.SimpleOffsetItemDecoration;
import com.kzaemrio.anread.databinding.ActivityMainBinding;
import com.kzaemrio.anread.model.Channel;
import com.kzaemrio.anread.model.Item;

import java.util.List;

public interface MainView {
    static MainView create(Context context) {

        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(context));
        binding.list.addItemDecoration(new SimpleDividerItemDecoration(context));
        binding.list.addItemDecoration(new SimpleOffsetItemDecoration());

        return new MainView() {

            private Callback mCallback;

            @Override
            public View getContentView() {
                return binding.getRoot();
            }

            @Override
            public void showLoading(boolean isShow) {
                binding.swipe.setRefreshing(isShow);
            }

            @Override
            public void showAddSubscription(boolean isShow) {
                if (isShow) {
                    binding.list.setAdapter(new AddSubscriptionAdapter(mCallback::onAddSubscriptionClick));
                }
            }

            @Override
            public void bind(List<Item> items) {
                binding.list.setAdapter(new MainAdapter(items, mCallback::onItemClick));
            }

            @Override
            public void showSyncToast(boolean is) {
                Snackbar.make(
                        binding.getRoot(),
                        is ? R.string.toast_sync_on : R.string.toast_sync_cancel,
                        Snackbar.LENGTH_SHORT
                ).show();
            }

            @Override
            public void setCallback(Callback callback) {
                mCallback = callback;
                binding.swipe.setOnRefreshListener(callback::onRefresh);
            }
        };
    }

    View getContentView();

    void setCallback(Callback callback);

    void showLoading(boolean isShow);

    void showAddSubscription(boolean isShow);

    void bind(List<Item> items);

    void showSyncToast(boolean is);

    interface Callback {
        void onAddSubscriptionClick();

        void onRefresh();

        void onItemClick(Item item);
    }

    interface ViewItem {
        static ViewItem create(int countUnRead, int countFav, List<ViewItemSub> itemSubs) {
            return new ViewItem() {
                @Override
                public int getUnReadCount() {
                    return countUnRead;
                }

                @Override
                public int getFavCount() {
                    return countFav;
                }

                @Override
                public List<ViewItemSub> getViewItemSubList() {
                    return itemSubs;
                }
            };
        }

        int getUnReadCount();

        int getFavCount();

        List<ViewItemSub> getViewItemSubList();

        interface ViewItemSub {
            static ViewItemSub create(Channel channel, int countUnRead) {
                return new ViewItemSub() {
                    @Override
                    public Channel getSubscription() {
                        return channel;
                    }

                    @Override
                    public int getUnReadCount() {
                        return countUnRead;
                    }
                };
            }

            Channel getSubscription();

            int getUnReadCount();
        }
    }
}
