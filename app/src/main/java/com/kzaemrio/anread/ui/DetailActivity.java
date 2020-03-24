package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.ActivityDetailBinding;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

public class DetailActivity extends BaseActivity {

    private static final String EXTRA_LINK = "EXTRA_LINK";

    private MutableLiveData<Item> mItem;

    public static Intent createIntent(Activity activity, String link) {
        return new Intent(activity, DetailActivity.class).putExtra(EXTRA_LINK, link);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DetailView detailView = DetailView.create(this);
        setContentView(detailView.getContentView());

        mItem = new MutableLiveData<>();
        mItem.observe(this, item -> {
            invalidateOptionsMenu();
            String pubDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(item.mPubDate), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
            String html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                    "<h3>" + item.mTitle + "</h3>" +
                    "<p/>" +
                    String.format("<p style=\"color:#%06X\">", 0xFFFFFF & ContextCompat.getColor(this, R.color.colorAccent)) + item.mChannelName + "\t" + pubDate + "</p>" +
                    "<p/>" +
                    item.mDes;
            detailView.bind(html);
        });

        String link = getIntent().getStringExtra(EXTRA_LINK);
        if (!TextUtils.isEmpty(link)) {
            Actions.executeOnDiskIO(() -> {
                mItem.postValue(AppDatabaseHolder.of(this).itemDao().query(link));
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        boolean isShowMenu = mItem.getValue() != null;
        menu.findItem(R.id.open).setVisible(isShowMenu);
        menu.findItem(R.id.share).setVisible(isShowMenu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open:
                Intent openIntent = new Intent();
                openIntent.setAction(Intent.ACTION_VIEW);
                openIntent.setData(Uri.parse(mItem.getValue().mLink));
                if (openIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(openIntent, getString(R.string.action_open)));
                }
                break;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String value = mItem.getValue().mTitle + "\n---\n" + mItem.getValue().mLink;
                sendIntent.putExtra(Intent.EXTRA_TEXT, value);
                sendIntent.setType("text/plain");
                if (sendIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.action_share)));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private interface DetailView {
        static DetailView create(Context context) {
            ActivityDetailBinding binding = ActivityDetailBinding.inflate(LayoutInflater.from(context));
            WebSettings settings = binding.web.getSettings();
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            settings.setAppCachePath(context.getCacheDir().getAbsolutePath());
            settings.setAppCacheEnabled(true);
            return new DetailView() {
                @Override
                public View getContentView() {
                    return binding.getRoot();
                }

                @Override
                public void bind(String html) {
                    binding.web.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);
                            binding.swipe.setRefreshing(true);
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            binding.swipe.setRefreshing(false);
                            binding.swipe.setEnabled(false);
                        }
                    });
                    binding.web.loadDataWithBaseURL(
                            "file:///android_asset/",
                            html,
                            "text/html",
                            "UTF-8",
                            null
                    );
                }

            };
        }

        View getContentView();

        void bind(String html);
    }
}
