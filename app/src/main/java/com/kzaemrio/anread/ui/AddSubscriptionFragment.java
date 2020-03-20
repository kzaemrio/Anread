package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.databinding.AdapterAddChannelBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

public class AddSubscriptionFragment extends Fragment {

    private static final String TAG = "AddSubscriptionFragment";

    public static void attach(FragmentActivity activity, int frameId) {
        activity.getSupportFragmentManager().beginTransaction().replace(
                frameId,
                new AddSubscriptionFragment(),
                TAG
        ).commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AdapterAddChannelBinding binding = AdapterAddChannelBinding.inflate(inflater);
        binding.icon.setOnClickListener(v -> Router.toAddSubscription(this));
        binding.text.setOnClickListener(v -> Router.toAddSubscription(this));
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Router.REQUEST_CODE_ADD_SUBSCRIPTION && resultCode == Activity.RESULT_OK) {
            Actions.executeOnDiskIO(() -> {
                new ViewModelProvider(requireActivity()).get(MainViewModel.class).updateChannelList();
            });
        }
    }

    private interface Router {

        int REQUEST_CODE_ADD_SUBSCRIPTION = 1;

        static void toAddSubscription(Fragment fragment) {
            fragment.startActivityForResult(
                    AddChannelActivity.createIntent(fragment.getContext()),
                    REQUEST_CODE_ADD_SUBSCRIPTION
            );
        }
    }
}
