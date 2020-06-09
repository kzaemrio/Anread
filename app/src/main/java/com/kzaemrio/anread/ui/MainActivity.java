package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kzaemrio.anread.CacheFeedWorker;
import com.kzaemrio.anread.R;
import com.kzaemrio.anread.model.Channel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends BaseActivity {

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        MainView view = MainView.create(this);
        setContentView(view.getContentView());

        mViewModel.getIsSyncOn().observe(this, is -> {
            CacheFeedWorker.update(getApplication().getApplicationContext(), is);

            invalidateOptionsMenu();

            if (mViewModel.getChannelList().getValue() != null) {
                view.showSyncToast(is);
            }
        });

        mViewModel.getChannelList().observe(this, list -> {
            if (list.isEmpty()) {
                view.showAddSubscription();
            } else {
                invalidateOptionsMenu();
                view.bind(list);
            }
        });
    }

    @Override
    protected boolean isDisplayHomeAsUpEnabled() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem itemSync = menu.findItem(R.id.sync);
        MenuItem itemRss = menu.findItem(R.id.rss);

        Boolean isSyncOn = mViewModel.getIsSyncOn().getValue();
        boolean isSyncOnValue = isSyncOn != null && isSyncOn;
        itemSync.setTitle(isSyncOnValue ? R.string.menu_to_sync_cancel : R.string.menu_to_sync);
        itemSync.setIcon(isSyncOnValue ? R.drawable.ic_sync_24dp : R.drawable.ic_sync_disabled_24dp);

        List<Channel> channelList = mViewModel.getChannelList().getValue();
        boolean hasChannelList = channelList != null && channelList.size() > 0;

        itemSync.setVisible(hasChannelList);
        itemRss.setVisible(hasChannelList);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rss:
                Router.toSubscriptionList(this);
                return true;
            case R.id.sync:
                mViewModel.switchSync();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private interface Router {
        static void toSubscriptionList(Activity activity) {
            activity.startActivity(ChannelListActivity.createIntent(activity));
        }
    }
}
