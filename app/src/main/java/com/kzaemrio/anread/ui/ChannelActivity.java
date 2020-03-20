package com.kzaemrio.anread.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kzaemrio.anread.databinding.ActivityChannelBinding;

import java.util.Collections;

import androidx.annotation.Nullable;

public class ChannelActivity extends BaseActivity {

    private static final String EXTRA_TITLE = "EXTRA_TITLE";
    private static final String EXTRA_URL = "EXTRA_URL";

    public static Intent createIntent(Context context, String channelTitle, String channelUrl) {
        return new Intent(context, ChannelActivity.class)
                .putExtra(EXTRA_TITLE, channelTitle)
                .putExtra(EXTRA_URL, channelUrl);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        setTitle(intent.getStringExtra(EXTRA_TITLE));

        ActivityChannelBinding binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ItemListFragment.attach(
                this,
                binding.frame.getId(),
                Collections.singletonList(intent.getStringExtra(EXTRA_URL))
        );
    }
}
