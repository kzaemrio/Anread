package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.kzaemrio.anread.adapter.MainAdapter;
import com.kzaemrio.anread.adapter.SimpleDividerItemDecoration;
import com.kzaemrio.anread.adapter.SimpleOffsetItemDecoration;
import com.kzaemrio.anread.databinding.ActivityChannelBinding;
import com.kzaemrio.anread.model.Item;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class ChannelActivity extends BaseActivity {

    private static final String EXTRA_TITLE = "EXTRA_TITLE";
    private static final String EXTRA_URL = "EXTRA_URL";

    public static Intent createIntent(Context context, String channelTitle, String channelUrl) {
        return new Intent(context, ChannelActivity.class)
                .putExtra(EXTRA_TITLE, channelTitle)
                .putExtra(EXTRA_URL, channelUrl);
    }

    private ChannelViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra(EXTRA_TITLE));

        ChannelView channelView = ChannelView.create(this);
        setContentView(channelView.getContentView());

        mViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);
        mViewModel.getList().observe(this, items -> {
            channelView.bind(items);
            setResult(RESULT_OK);
        });
        mViewModel.getIsShowLoading().observe(this, channelView::showLoading);

        String url = intent.getStringExtra(EXTRA_URL);

        channelView.setCallback(new ChannelView.Callback() {
            @Override
            public void onItemClick(Item item) {
                setResult(RESULT_OK);
                Router.toDetail(ChannelActivity.this, item.mLink);
            }

            @Override
            public void onRefresh() {
                mViewModel.requestList(url);
            }
        });

        mViewModel.requestList(url);
    }

    private interface Router {
        static void toDetail(Activity activity, String link) {
            activity.startActivity(DetailActivity.createIntent(activity, link));
        }
    }

    private interface ChannelView {
        static ChannelView create(Context context) {

            ActivityChannelBinding binding = ActivityChannelBinding.inflate(LayoutInflater.from(context));
            binding.list.addItemDecoration(new SimpleDividerItemDecoration(context));
            binding.list.addItemDecoration(new SimpleOffsetItemDecoration());

            return new ChannelView() {

                private Callback mCallback;

                @Override
                public View getContentView() {
                    return binding.getRoot();
                }

                @Override
                public void bind(List<Item> items) {
                    // TODO: use ItemListFragment
//                    binding.list.setAdapter(new MainAdapter(items, mCallback::onItemClick));
                }

                @Override
                public void showLoading(boolean isShow) {
                    binding.swipe.setRefreshing(isShow);
                }

                @Override
                public void setCallback(Callback callback) {
                    mCallback = callback;
                    binding.swipe.setOnRefreshListener(mCallback::onRefresh);
                }
            };
        }

        View getContentView();

        void bind(List<Item> items);

        void showLoading(boolean isShow);

        void setCallback(Callback callback);

        interface Callback {
            void onItemClick(Item item);

            void onRefresh();
        }
    }
}
