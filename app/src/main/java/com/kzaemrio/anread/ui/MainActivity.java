package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

        MenuItem itemRss = menu.findItem(R.id.rss);

        List<Channel> channelList = mViewModel.getChannelList().getValue();
        boolean hasChannelList = channelList != null && channelList.size() > 0;

        itemRss.setVisible(hasChannelList);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rss:
                Router.toSubscriptionList(this);
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
