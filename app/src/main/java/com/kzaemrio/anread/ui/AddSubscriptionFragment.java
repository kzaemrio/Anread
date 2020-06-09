package com.kzaemrio.anread.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.databinding.AdapterAddChannelBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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

    private interface Router {
        static void toAddSubscription(Fragment fragment) {
            fragment.startActivity(AddChannelActivity.createIntent(fragment.getContext()));
        }
    }
}
