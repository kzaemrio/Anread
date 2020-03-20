package com.kzaemrio.anread.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

public class ItemListFragment extends Fragment {

    public static final String TAG = "ItemListFragment";

    private static final String KEY_CHANNEL_LIST = "KEY_CHANNEL_LIST";

    private ItemListView mView;
    private ItemListViewModel mModel;

    public static void attach(FragmentActivity activity, int frameId, List<String> channelUrls) {
        Fragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(KEY_CHANNEL_LIST, new ArrayList<>(channelUrls));
        fragment.setArguments(args);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(frameId, fragment, TAG)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = ItemListView.create(inflater.getContext());
        return mView.getContentView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mModel = new ViewModelProvider(this).get(ItemListViewModel.class);

        mView.setCallback(new ItemListView.Callback() {
            @Override
            public void onRefresh() {
                mModel.saveItemPosition(mView.getAdapterPosition(), mView.getOffset());
                mModel.updateItemList();
            }

            @Override
            public void onClick(Item item) {
                Router.toDetail(ItemListFragment.this, item.mLink);
            }
        });

        mModel.getIsShowLoading().observe(getViewLifecycleOwner(), mView::showLoading);
        mModel.getItemList().observe(getViewLifecycleOwner(), mView::bind);
        mModel.getItemPosition().observe(getViewLifecycleOwner(), mView::scrollTo);
        mModel.setChannelList(Objects.requireNonNull(getArguments()).getStringArrayList(KEY_CHANNEL_LIST));
        mModel.updateItemList();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isRemoving()) {
            mModel.saveItemPosition(mView.getAdapterPosition(), mView.getOffset());
        }
    }

    private interface Router {
        static void toDetail(Fragment fragment, String link) {
            fragment.startActivity(DetailActivity.createIntent(fragment.requireActivity(), link));
        }
    }
}
