package com.kzaemrio.anread.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.kzaemrio.anread.adapter.AddSubscriptionAdapter;
import com.kzaemrio.anread.databinding.ActivityMainBinding;

public interface MainView {
    static MainView create(Context context) {

        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(context));

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
            public void setCallback(Callback callback) {
                mCallback = callback;
            }
        };
    }

    View getContentView();

    void setCallback(Callback callback);

    void showLoading(boolean isShow);

    void showAddSubscription(boolean isShow);

    interface Callback {
        void onAddSubscriptionClick();
    }
}
