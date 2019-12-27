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
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.ActivityDetailBinding;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemDao;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {

    private static final String EXTRA_LINK = "EXTRA_LINK";

    private Item mItem;

    public static Intent createIntent(Activity activity, String link) {
        return new Intent(activity, DetailActivity.class).putExtra(EXTRA_LINK, link);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DetailView detailView = DetailView.create(this);
        setContentView(detailView.getContentView());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String link = getIntent().getStringExtra(EXTRA_LINK);
        if (!TextUtils.isEmpty(link)) {
            Observable.just(link)
                    .map(url -> {
                        ItemDao dao = AppDatabaseHolder.of(this).itemDao();
                        Item item = dao.query(link);
                        item.mIsRead = 1;
                        dao.insertReplace(item);
                        return item;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(item -> mItem = item)
                    .map(item -> "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                            "<h3>" + item.mTitle + "</h3>" +
                            "<p/>" +
                            "<p>" + item.mChannelName + "\t" + item.mPubDateDetail + "</p>" +
                            "<p/>" +
                            item.mDesDetail
                    )
                    .doOnNext(detailView::bind)
                    .subscribe();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.open:
                Intent openIntent = new Intent();
                openIntent.setAction(Intent.ACTION_VIEW);
                openIntent.setData(Uri.parse(mItem.mLink));
                if (openIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(openIntent, getString(R.string.action_open)));
                }
                break;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String value = mItem.mTitle + "\n---\n" + mItem.mLink;
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
