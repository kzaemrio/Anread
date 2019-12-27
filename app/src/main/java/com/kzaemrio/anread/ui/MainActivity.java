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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

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
        mViewModel.isShowAddSubscription().observe(this, view::showAddSubscription);
        mViewModel.getItemList().observe(this, view::bind);

        mViewModel.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
        int REQUEST_CODE_TO_DETAIL = 2;
        int REQUEST_CODE_SUBSCRIPTION_LIST = 3;

        static void toAddSubscription(Activity activity) {
            activity.startActivityForResult(
                    AddSubscriptionActivity.createIntent(activity),
                    REQUEST_CODE_ADD_SUBSCRIPTION
            );
        }

        static void toDetail(Activity activity, String link) {
            activity.startActivityForResult(
                    DetailActivity.createIntent(activity, link),
                    REQUEST_CODE_TO_DETAIL
            );
        }

        static void toSubscriptionList(Activity activity) {
            activity.startActivityForResult(
                    SubscriptionListActivity.createIntent(activity),
                    REQUEST_CODE_SUBSCRIPTION_LIST
            );
        }
    }
}
