package com.kzaemrio.ithome;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kzaemrio.ithome.databinding.ActivityMainBinding;
import com.kzaemrio.ithome.databinding.DialogCacheBinding;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class MainView {

    private final Context mContext;

    @Inject
    ItemListAdapter mAdapter;

    LinearLayoutManager mLayoutManager;

    ActivityMainBinding mBinding;

    @Inject
    WebHelper mWebHelper;

    @Inject
    public MainView(@ActivityContext Context context) {
        mContext = context;
    }

    public void init() {
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(mContext));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                ViewCompat.postOnAnimationDelayed(
                        mBinding.list,
                        () -> mBinding.list.smoothScrollBy(0, -16),
                        100L
                );
            }
        });

        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setStackFromEnd(true);

        mBinding.list.setHasFixedSize(true);
        mBinding.list.setLayoutManager(mLayoutManager);
        mBinding.list.setAdapter(mAdapter);
    }

    public View getContentView() {
        return mBinding.getRoot();
    }

    public void showIsLoading(boolean isShow) {
        mBinding.refresh.setRefreshing(isShow);
    }

    public void bind(List<ItemListAdapter.ViewItem> list) {
        mAdapter.submitList(list);
    }

    public void setCallback(Callback callback) {
        mBinding.refresh.setOnRefreshListener(callback::onRefresh);
        mAdapter.setOnItemClickAction(callback::onItemClick);
        mAdapter.setOnItemLongClickAction(callback::onItemLongClick);
    }

    public int getOffset() {
        return mBinding.list.getChildAt(0).getTop();
    }

    public void scrollToPositionWithOffset(ScrollPosition position) {
        mLayoutManager.scrollToPositionWithOffset(position.position(), position.offset());
    }

    public boolean hasData() {
        return mAdapter.getItemCount() > 0;
    }

    public int getFirstPosition() {
        return mBinding.list.getChildAdapterPosition(mBinding.list.getChildAt(0));
    }

    public void showCacheDialog(List<ItemListAdapter.ViewItem> list) {
        DialogCacheBinding binding = DialogCacheBinding.inflate(LayoutInflater.from(mContext));

        AlertDialog dialog = new AlertDialog.Builder(mContext).setView(binding.getRoot())
                .setNegativeButton(android.R.string.cancel, (dialogInterface, which) -> {

                })
                .create();

        int size = list.size();
        int[] index = {0};

        binding.bar.setMax(size);
        Iterator<ItemListAdapter.ViewItem> iterator = list.iterator();
        Runnable next = () -> {
            index[0] = index[0] + 1;

            Item item = iterator.next().getItem();

            int progress = index[0];

            binding.bar.setProgress(progress);
            binding.text.setText(String.format(
                    "%s/%s %s",
                    progress, size,
                    item.getTitle()
            ));

            mWebHelper.showDetail(binding.web, item);
        };

        mWebHelper.cacheEnabledWeb(binding.web).setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (iterator.hasNext()) {
                    next.run();
                } else {
                    dialog.cancel();
                }
            }
        });

        dialog.setOnShowListener(dialogInterface -> {
            next.run();
        });

        dialog.setOnDismissListener(dialogInterface -> {
            binding.web.destroy();
        });

        dialog.show();
    }

    public interface Callback {
        void onRefresh();

        void onItemClick(ItemListAdapter.ViewItem item);

        void onItemLongClick(ItemListAdapter.ViewItem item);
    }

    public interface ScrollPosition {
        static ScrollPosition create(int position, int offset) {
            return new ScrollPosition() {
                @Override
                public int position() {
                    return position;
                }

                @Override
                public int offset() {
                    return offset;
                }
            };
        }

        int position();

        int offset();
    }
}
