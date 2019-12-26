package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Item;

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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String link = getIntent().getStringExtra(EXTRA_LINK);
        if (!TextUtils.isEmpty(link)) {
            Observable.just(link)
                    .map(url -> AppDatabaseHolder.of(this).itemDao().query(link))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(item -> mItem = item)
                    .map(item -> "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                            "<h3>" + item.mTitle + "</h3>" +
                            "<p/>" +
                            "<p>" + item.mChannelName + "\t" + item.mPubDate + "</p>" +
                            "<p/>" +
                            item.mDes
                    )
                    .doOnNext(html -> {
                        WebView webView = new WebView(this);
                        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
                        setContentView(webView);
                    })
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
}
