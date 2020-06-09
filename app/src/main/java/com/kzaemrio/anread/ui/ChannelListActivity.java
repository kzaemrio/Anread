package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.adapter.ChannelAdapter;
import com.kzaemrio.anread.adapter.SimpleDividerItemDecoration;
import com.kzaemrio.anread.model.Channel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Consumer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChannelListActivity extends BaseActivity {

    private ChannelListViewModel mViewModel;

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, ChannelListActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ChannelListViewModel.class);

        MvpView view = MvpView.create(
                this,
                channel -> Router.toChannel(this, channel.getTitle(), channel.getUrl()),
                channel -> mViewModel.delete(channel)
        );
        setContentView(view.getContentView());

        mViewModel.getChannelList().observe(this, view::bind);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscription, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Router.toAddChannel(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private interface Router {
        static void toAddChannel(Activity activity) {
            activity.startActivity(AddChannelActivity.createIntent(activity));
        }

        static void toChannel(Activity activity, String channelTitle, String channelUrl) {
            activity.startActivity(ChannelActivity.createIntent(activity, channelTitle, channelUrl));
        }
    }

    private interface MvpView {
        static MvpView create(Context context, Consumer<Channel> clickConsumer, Consumer<Channel> longClickConsumer) {

            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));

            return new MvpView() {
                @Override
                public View getContentView() {
                    return recyclerView;
                }

                @Override
                public void bind(List<Channel> channels) {
                    recyclerView.setAdapter(new ChannelAdapter(channels, clickConsumer, this::showDeleteDialog));
                }

                private void showDeleteDialog(Channel channel) {
                    new AlertDialog.Builder(context)
                            .setTitle(channel.getTitle())
                            .setPositiveButton(R.string.delete, (dialog, which) -> longClickConsumer.accept(channel))
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            })
                            .create()
                            .show();
                }
            };
        }

        View getContentView();

        void bind(List<Channel> channels);
    }
}
