package com.kzaemrio.ithome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kzaemrio.ithome.databinding.ActivityMainBinding;
import com.kzaemrio.ithome.event.OnHideWebViewLoadingEvent;
import com.kzaemrio.ithome.event.OnShowWebViewLoadingEvent;
import com.kzaemrio.ithome.event.OnWebViewHideStartEvent;
import com.kzaemrio.ithome.event.OnWebViewShowEndEvent;
import com.kzaemrio.simplebus.lib.SimpleBus;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

public class MainView {

    private static final int duration = 200;

    private final Context mContext;

    private final ItemListAdapter mAdapter;

    private final LinearLayoutManager mLayoutManager;

    private final ActivityMainBinding mBinding;

    public MainView(Context context) {
        mContext = context;

        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(mContext));

        mAdapter = new ItemListAdapter();
        mAdapter.setOnItemClickAction(this::showDetail);
        mAdapter.setOnItemLongClickAction(this::shareItem);

        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setStackFromEnd(true);

        mBinding.list.setLayoutManager(mLayoutManager);
        mBinding.list.setAdapter(mAdapter);

        mBinding.progress.setIndeterminateTintList(ColorStateList.valueOf(mContext.getColor(R.color.colorAccent)));

        mBinding.web.setWebViewClient(new MyWebViewClient());
    }

    private void shareItem(Item item) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String value = item.getTitle() + "\n---\n" + item.getLink();
        sendIntent.putExtra(Intent.EXTRA_TEXT, value);
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(Intent.createChooser(sendIntent, mContext.getString(R.string.action_share)));
        }
    }

    public View getContentView() {
        return mBinding.frame;
    }

    public void showRefresh() {
        mBinding.refresh.setRefreshing(true);
    }

    public void hideRefresh() {
        mBinding.refresh.setRefreshing(false);
    }

    public void bind(List<ItemListAdapter.ViewItem> list) {
        mAdapter.submitList(list);
    }

    public void setCallback(Callback callback) {
        mBinding.refresh.setOnRefreshListener(callback::onRefresh);
    }

    public boolean isWebViewShow() {
        return mBinding.box.isShown();
    }

    private void showDetail(Item item) {
        String pubDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(item.getPubDate()), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        String html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                "<h3>" + item.getTitle() + "</h3>" +
                "<p/>" +
                String.format("<p style=\"color:#%06X\">", 0xFFFFFF & ContextCompat.getColor(mContext, R.color.text_color_label)) + item.getChannelName() + "\t" + pubDate + "</p>" +
                "<p/>" +
                item.getDes();

        mBinding.web.loadDataWithBaseURL(
                "file:///android_asset/",
                html,
                "text/html",
                "UTF-8",
                null
        );

        ObjectAnimator animator = ObjectAnimator.ofFloat(mBinding.box, View.TRANSLATION_Y, mBinding.frame.getHeight(), 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mBinding.box.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                SimpleBus.getDefault().post(new OnWebViewShowEndEvent());
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    public void hideWebView() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mBinding.box, View.TRANSLATION_Y, 0, mBinding.frame.getHeight());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                SimpleBus.getDefault().post(new OnWebViewHideStartEvent());
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mBinding.box.setVisibility(View.GONE);
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    public void destroyWebView() {
        // Make sure you remove the WebView from its parent view before doing anything.
        mBinding.box.removeAllViews();

        mBinding.web.clearHistory();

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        mBinding.web.clearCache(true);

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        mBinding.web.loadUrl("about:blank");

        mBinding.web.onPause();
        mBinding.web.removeAllViews();

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        mBinding.web.pauseTimers();
    }

    public long getPubDate() {
        View childAt = mBinding.list.getChildAt(0);
        int position = mBinding.list.getChildAdapterPosition(childAt);
        return mAdapter.getCurrentList().get(position).getItem().getPubDate();
    }

    public int getOffset() {
        return mBinding.list.getChildAt(0).getTop();
    }

    public boolean isInit() {
        return mAdapter.getItemCount() > 0;
    }

    public void scrollToPositionWithOffset(int index, int offset) {
        mLayoutManager.scrollToPositionWithOffset(index, offset);
    }

    public void alertHasNew() {
        mBinding.list.postDelayed(() -> mBinding.list.smoothScrollBy(0, -50), 300);
    }

    public void showWebViewLoading() {
        mBinding.progress.setVisibility(View.VISIBLE);
    }

    public void hideWebViewLoading() {
        mBinding.progress.setVisibility(View.GONE);
    }

    public void hideList() {
        mBinding.refresh.setVisibility(View.GONE);
    }

    public void showList() {
        mBinding.refresh.setVisibility(View.VISIBLE);
    }

    public interface Callback {
        void onRefresh();
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            view.setVisibility(View.GONE);
            SimpleBus.getDefault().post(new OnShowWebViewLoadingEvent());
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            view.setVisibility(View.VISIBLE);
            SimpleBus.getDefault().post(new OnHideWebViewLoadingEvent());
        }
    }
}
