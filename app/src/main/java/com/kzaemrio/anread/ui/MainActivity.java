package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.model.Item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends BaseActivity {

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        MainView view = MainView.create(this);

        view.setCallback(new MainView.Callback() {
            @Override
            public void onAddSubscriptionClick() {
                Router.toAddSubscription(MainActivity.this);
            }

            @Override
            public void onRefresh() {
                mViewModel.init();
            }

            @Override
            public void onItemClick(Item item) {
                Router.toDetail(MainActivity.this, item.mLink);
            }

        });
        setContentView(view.getContentView());

        mViewModel.isShowLoading().observe(this, view::showLoading);
        mViewModel.isShowAddSubscription().observe(this, is -> {
            invalidateOptionsMenu();
            view.showAddSubscription(is);
        });
        mViewModel.getItemList().observe(this, view::bind);
        mViewModel.getIsSyncOn().observe(this, is -> invalidateOptionsMenu());

        mViewModel.init();
    }

    @Override
    protected boolean isDisplayHomeAsUpEnabled() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        Boolean isShow = mViewModel.isShowAddSubscription().getValue();
        boolean visible = isShow != null && !isShow;
        MenuItem item = menu.findItem(R.id.sync);
        Boolean isSyncOn = mViewModel.getIsSyncOn().getValue();
        item.setIcon(isSyncOn != null && isSyncOn ? R.drawable.ic_sync_24dp : R.drawable.ic_sync_disabled_24dp);
        item.setVisible(visible);
        menu.findItem(R.id.read).setVisible(visible);
        menu.findItem(R.id.rss).setVisible(visible);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.read:
                mViewModel.readAll();
                return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Router.REQUEST_CODE_ADD_SUBSCRIPTION:
                case Router.REQUEST_CODE_SUBSCRIPTION_LIST:
                    mViewModel.init();
                    break;
            }
        }
    }

    private interface Router {

        int REQUEST_CODE_ADD_SUBSCRIPTION = 1;
        int REQUEST_CODE_SUBSCRIPTION_LIST = 2;

        static void toAddSubscription(Activity activity) {
            activity.startActivityForResult(
                    AddChannelActivity.createIntent(activity),
                    REQUEST_CODE_ADD_SUBSCRIPTION
            );
        }

        static void toDetail(Activity activity, String link) {
            activity.startActivity(DetailActivity.createIntent(activity, link));
        }

        static void toSubscriptionList(Activity activity) {
            activity.startActivityForResult(
                    ChannelListActivity.createIntent(activity),
                    REQUEST_CODE_SUBSCRIPTION_LIST
            );
        }
    }
}
