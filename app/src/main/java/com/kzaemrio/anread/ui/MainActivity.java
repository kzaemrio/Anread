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
    private MainView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        mView = MainView.create(this);

        Router router = Router.create(this);
        mView.setCallback(new MainView.Callback() {
            @Override
            public void onAddSubscriptionClick() {
                router.toAddSubscription();
            }

            @Override
            public void onRefresh() {
                mViewModel.init();
            }

            @Override
            public void onItemClick(Item item) {
                router.toDetail(item.mLink);
            }

        });
        setContentView(mView.getContentView());

        mViewModel.isShowLoading().observe(this, mView::showLoading);
        mViewModel.isShowAddSubscription().observe(this, mView::showAddSubscription);
        mViewModel.getItemList().observe(this, mView::bind);

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
                    mViewModel.init();
                    break;
            }
        }
    }

    private interface Router {

        int REQUEST_CODE_ADD_SUBSCRIPTION = 1;
        int REQUEST_CODE_TO_DETAIL = 2;

        static Router create(Activity activity) {
            return () -> activity;
        }

        Activity activity();

        default void toAddSubscription() {
            activity().startActivityForResult(
                    AddSubscriptionActivity.createIntent(activity()),
                    REQUEST_CODE_ADD_SUBSCRIPTION
            );
        }

        default void toDetail(String link) {
            activity().startActivityForResult(
                    DetailActivity.createIntent(activity(), link),
                    REQUEST_CODE_TO_DETAIL
            );
        }
    }
}
