package com.kzaemrio.anread.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.ActivityMainBinding;
import com.kzaemrio.anread.model.Channel;

import java.util.ArrayList;
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
                activity.getSupportFragmentManager().beginTransaction().replace(
                        binding.frame.getId(),
                        new AddSubscriptionFragment(),
                        AddSubscriptionFragment.TAG
                ).commit();
            }

            @Override
            public void bind(List<Channel> items) {
                ArrayList<String> channels = new ArrayList<>(items.size());
                for (Channel channel : items) {
                    channels.add(channel.getUrl());
                }
                activity.getSupportFragmentManager().beginTransaction().replace(
                        binding.frame.getId(),
                        ItemListFragment.create(channels),
                        ItemListFragment.TAG
                ).commit();
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
