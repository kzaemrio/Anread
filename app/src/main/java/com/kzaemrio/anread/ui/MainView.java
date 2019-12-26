package com.kzaemrio.anread.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.kzaemrio.anread.adapter.AddSubscriptionAdapter;
import com.kzaemrio.anread.adapter.MainAdapter;
import com.kzaemrio.anread.adapter.SimpleItemDecoration;
import com.kzaemrio.anread.databinding.ActivityMainBinding;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.Subscription;

import java.util.List;

import androidx.recyclerview.widget.DividerItemDecoration;

public interface MainView {
    static MainView create(Context context) {

        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(context));
        binding.list.addItemDecoration(new SimpleItemDecoration(context));

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
                binding.list.setAdapter(new MainAdapter(items));
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

    interface Callback {
        void onAddSubscriptionClick();

        void onRefresh();
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
            static ViewItemSub create(Subscription subscription, int countUnRead) {
                return new ViewItemSub() {
                    @Override
                    public Subscription getSubscription() {
                        return subscription;
                    }

                    @Override
                    public int getUnReadCount() {
                        return countUnRead;
                    }
                };
            }

            Subscription getSubscription();

            int getUnReadCount();
        }
    }
}
