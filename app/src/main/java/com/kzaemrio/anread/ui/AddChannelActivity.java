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
import com.kzaemrio.anread.model.Channel;

import androidx.annotation.Nullable;

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
            String input = view.getInput().toString();
            Actions.executeOnBackground(() -> {
                try {
                    Channel channel = Actions.getChannel(input);
                    AppDatabaseHolder.of(getApplication()).channelDao().insert(channel);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        setContentView(view.getContentView());
    }

    private interface AddChannelView {
        static AddChannelView create(Context context) {
            ActivityAddChannelBinding binding = ActivityAddChannelBinding.inflate(LayoutInflater.from(context));

            binding.input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    binding.bt.setEnabled(!TextUtils.isEmpty(s));
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
                    binding.bt.setEnabled(!isShow);
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
