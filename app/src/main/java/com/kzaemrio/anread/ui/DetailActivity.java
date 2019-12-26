package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.WebView;

import com.kzaemrio.anread.model.AppDatabaseHolder;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {

    private static final String EXTRA_LINK = "EXTRA_LINK";

    public static Intent createIntent(Activity activity, String link) {
        return new Intent(activity, DetailActivity.class).putExtra(EXTRA_LINK, link);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String link = getIntent().getStringExtra(EXTRA_LINK);
        if (!TextUtils.isEmpty(link)) {
            Observable.just(link)
                    .map(url -> AppDatabaseHolder.of(this).itemDao().query(link))
                    .map(item -> "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                            "<h3>" + item.mTitle + "</h3>" +
                            "<p/>" +
                            "<p>" + item.mChannelName + "&emsp;" + item.mPubDate + "</p>" +
                            "<p/>" +
                            item.mDes
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(html -> {
                        WebView webView = new WebView(this);
                        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
                        setContentView(webView);
                    })
                    .subscribe();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
