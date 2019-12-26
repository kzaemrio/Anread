package com.kzaemrio.anread.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.ActivityAddSubscriptionBinding;
import com.kzaemrio.anread.model.AppDatabase;
import com.kzaemrio.anread.model.AppDatabaseHolder;
import com.kzaemrio.anread.model.Feed;
import com.kzaemrio.anread.model.Item;
import com.kzaemrio.anread.model.ItemDao;
import com.kzaemrio.anread.model.Subscription;

import org.simpleframework.xml.core.Persister;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddSubscriptionActivity extends AppCompatActivity {
    public static Intent createIntent(Context context) {
        return new Intent(context, AddSubscriptionActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAddSubscriptionBinding binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_add_subscription
        );

        binding.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.bt.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.bt.setOnClickListener(v -> {
            Observable.just(Objects.requireNonNull(binding.input.getText()))
                    .map(Objects::toString)
                    .doOnNext(url -> {
                        Request request = new Request.Builder().url(url).build();
                        Response response = new OkHttpClient.Builder().build().newCall(request).execute();
                        Feed feed = new Persister().read(Feed.class, response.body().byteStream(), false);
                        Subscription channel = Subscription.create(url, feed.mFeedChannel.mTitle);

                        Item[] itemList = Observable.fromIterable(feed.mFeedChannel.mFeedItemList)
                                .map(i -> Item.create(i, url))
                                .toList()
                                .map(list -> list.toArray(new Item[list.size()]))
                                .blockingGet();
                        AppDatabase database = AppDatabaseHolder.of(getApplicationContext());
                        database.subscriptionDao().insert(channel);
                        ItemDao dao = database.itemDao();
                        dao.insert(itemList);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(url -> {
                        setResult(RESULT_OK, new Intent().putExtra("url", url));
                        finish();
                    })
                    .subscribe();
        });

        binding.input.setText("https://www.ithome.com/rss/");
    }
}
