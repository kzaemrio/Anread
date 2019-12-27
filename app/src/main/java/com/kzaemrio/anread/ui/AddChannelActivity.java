package com.kzaemrio.anread.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.kzaemrio.anread.Actions;
import com.kzaemrio.anread.databinding.ActivityAddChannelBinding;
import com.kzaemrio.anread.model.AppDatabaseHolder;

import java.util.Objects;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class AddChannelActivity extends BaseActivity {
    public static Intent createIntent(Context context) {
        return new Intent(context, AddChannelActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AddChannelView view = AddChannelView.create(this);
        view.setCallback(() -> {
            view.showLoading(true);

            Observable.just(Objects.requireNonNull(view.getInput()))
                    .map(Objects::toString)
                    .map(Actions::getRssResult)
                    .doOnNext(rssResult -> Actions.insertRssResult(AppDatabaseHolder.of(getApplicationContext().getApplicationContext()), rssResult))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(url -> {
                        view.showLoading(false);
                        setResult(RESULT_OK, new Intent().putExtra("url", url.getChannel().getUrl()));
                        finish();
                    })
                    .subscribe();
        });

        setContentView(view.getContentView());
    }

    private interface AddChannelView {
        static AddChannelView create(Context context) {
            ActivityAddChannelBinding binding = ActivityAddChannelBinding.inflate(LayoutInflater.from(context));

            BehaviorSubject<Boolean> isInputEmpty = BehaviorSubject.createDefault(true);
            BehaviorSubject<Boolean> isLoadingShow = BehaviorSubject.createDefault(false);

            Observable.merge(isInputEmpty, isLoadingShow)
                    .doOnNext(i -> binding.bt.setEnabled(!isInputEmpty.getValue() && !isLoadingShow.getValue()))
                    .subscribe();

            binding.input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    isInputEmpty.onNext(TextUtils.isEmpty(s));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            binding.input.setText("https://www.ithome.com/rss/");

            return new AddChannelView() {
                @Override
                public View getContentView() {
                    return binding.getRoot();
                }

                @Override
                public void setCallback(Callback callback) {
                    binding.bt.setOnClickListener(v -> callback.onBtClick());
                }

                @Override
                public CharSequence getInput() {
                    return binding.input.getText();
                }

                @Override
                public void showLoading(boolean isShow) {
                    isLoadingShow.onNext(isShow);
                    binding.progress.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
                }
            };
        }

        View getContentView();

        void setCallback(Callback callback);

        CharSequence getInput();

        void showLoading(boolean isShow);

        interface Callback {
            void onBtClick();
        }
    }
}
