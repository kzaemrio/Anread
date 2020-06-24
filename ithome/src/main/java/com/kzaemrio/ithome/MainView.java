package com.kzaemrio.ithome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kzaemrio.ithome.event.OnHideWebViewLoadingEvent;
import com.kzaemrio.ithome.event.OnShowWebViewLoadingEvent;

import org.greenrobot.eventbus.EventBus;
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

    private final RecyclerView mRecyclerView;

    private final SwipeRefreshLayout mRefreshLayout;

    private final ProgressBar mProgressBar;
    private final FrameLayout mWebViewBox;

    private final FrameLayout mFrameLayout;

    private WebView mWebView;

    public MainView(Context context) {
        mContext = context;

        mAdapter = new ItemListAdapter();
        mAdapter.setItemConsumer(this::showDetail);

        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView = new RecyclerView(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout = new SwipeRefreshLayout(mContext);
        mRefreshLayout.addView(mRecyclerView);

        mProgressBar = new ProgressBar(mContext);
        mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(mContext.getColor(R.color.colorAccent)));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100, Gravity.CENTER);

        mWebView = new WebView(mContext.getApplicationContext());
        mWebView.setWebViewClient(new MyWebViewClient());

        mWebViewBox = new FrameLayout(mContext);
        mWebViewBox.setBackgroundColor(Color.WHITE);
        mWebViewBox.addView(mProgressBar, params);
        mWebViewBox.addView(mWebView);
        mWebViewBox.setVisibility(View.GONE);

        mFrameLayout = new FrameLayout(mContext);
        mFrameLayout.addView(mRefreshLayout);
        mFrameLayout.addView(mWebViewBox);
    }

    public View getContentView() {
        return mFrameLayout;
    }

    public void showRefresh() {
        mRefreshLayout.setRefreshing(true);
    }

    public void hideRefresh() {
        mRefreshLayout.setRefreshing(false);
    }

    public void bind(List<ItemListAdapter.ViewItem> list) {
        mAdapter.submitList(list);
    }

    public void setCallback(Callback callback) {
        mRefreshLayout.setOnRefreshListener(callback::onRefresh);
    }

    public boolean isWebViewShow() {
        return mWebViewBox.isShown();
    }

    private void showDetail(Item item) {
        String pubDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(item.getPubDate()), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        String html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                "<h3>" + item.getTitle() + "</h3>" +
                "<p/>" +
                String.format("<p style=\"color:#%06X\">", 0xFFFFFF & ContextCompat.getColor(mContext, R.color.text_color_label)) + item.getChannelName() + "\t" + pubDate + "</p>" +
                "<p/>" +
                item.getDes();

        mWebView.loadDataWithBaseURL(
                "file:///android_asset/",
                html,
                "text/html",
                "UTF-8",
                null
        );

        ObjectAnimator animator = ObjectAnimator.ofFloat(mWebViewBox, View.TRANSLATION_Y, mFrameLayout.getHeight(), 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mWebViewBox.setVisibility(View.VISIBLE);
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    public void hideWebView() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mWebViewBox, View.TRANSLATION_Y, 0, mFrameLayout.getHeight());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mWebViewBox.setVisibility(View.GONE);
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    public void destroyWebView() {
        // Make sure you remove the WebView from its parent view before doing anything.
        mWebViewBox.removeAllViews();

        mWebView.clearHistory();

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        mWebView.clearCache(true);

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        mWebView.loadUrl("about:blank");

        mWebView.onPause();
        mWebView.removeAllViews();
        mWebView.destroyDrawingCache();

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        mWebView.pauseTimers();

        // Null out the reference so that you don't end up re-using it.
        mWebView = null;
    }

    public long getPubDate() {
        View childAt = mRecyclerView.getChildAt(0);
        int position = mRecyclerView.getChildAdapterPosition(childAt);
        return mAdapter.getCurrentList().get(position).getItem().getPubDate();
    }

    public int getOffset() {
        return mRecyclerView.getChildAt(0).getTop();
    }

    public boolean isInit() {
        return mAdapter.getItemCount() > 0;
    }

    public void scrollToPositionWithOffset(int index, int offset) {
        mLayoutManager.scrollToPositionWithOffset(index, offset);
    }

    public void alertHasNew() {
        mRecyclerView.postDelayed(() -> mRecyclerView.smoothScrollBy(0, -50), 300);
    }

    public void showWebViewLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideWebViewLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    public interface Callback {
        void onRefresh();
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            view.setVisibility(View.GONE);
            EventBus.getDefault().post(new OnShowWebViewLoadingEvent());
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            view.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new OnHideWebViewLoadingEvent());
        }
    }
}
