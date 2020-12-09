package com.kzaemrio.ithome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kzaemrio.ithome.db.AppDataBaseHolder;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailActivity extends ComponentActivity {

    private static final String EXTRA_LINK = "EXTRA_LINK";
    private static final String EXTRA_TITLE = "EXTRA_TITLE";

    public static Intent createIntent(Context context, String link, String title) {
        return new Intent(context, DetailActivity.class)
                .putExtra(EXTRA_LINK, link)
                .putExtra(EXTRA_TITLE, title);
    }



    @Inject
    WebHelper mWebHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(true);

        AppDataBaseHolder.getInstance(getApplication())
                .itemDao()
                .query(getIntent().getStringExtra(EXTRA_LINK))
                .observe(this, this::bind);
    }

    private void bind(Item item) {
        SwipeRefreshLayout refreshLayout = new SwipeRefreshLayout(this);

        WebView webView = mWebHelper.cacheEnabledWeb(new WebView(getApplicationContext()));

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
            }
        });

        refreshLayout.addView(webView);

        mWebHelper.showDetail(webView, item);

        setContentView(refreshLayout);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onMenuItemSelected(featureId, item);
        }
    }
}
