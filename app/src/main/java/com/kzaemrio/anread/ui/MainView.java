package com.kzaemrio.anread.ui;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.ActivityMainBinding;
import com.kzaemrio.anread.model.Channel;

import java.util.LinkedList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

public interface MainView {
    static MainView create(FragmentActivity activity) {

        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(activity));

        return new MainView() {
            @Override
            public View getContentView() {
                return binding.getRoot();
            }

            @Override
            public void showAddSubscription() {
                AddSubscriptionFragment.attach(activity, binding.frame.getId());
            }

            @Override
            public void bind(List<Channel> items) {
                LinkedList<String> strings = new LinkedList<>();
                for (Channel item : items) {
                    strings.add(item.getUrl());
                }

                ItemListFragment.attach(activity, binding.frame.getId(), strings);
            }

            @Override
            public void showSyncToast(boolean is) {
                Snackbar.make(
                        binding.getRoot(),
                        is ? R.string.toast_sync_on : R.string.toast_sync_cancel,
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        };
    }

    View getContentView();

    void showAddSubscription();

    void bind(List<Channel> items);

    void showSyncToast(boolean is);
}
