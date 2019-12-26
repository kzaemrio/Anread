package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Router router = Router.create(this);

        MainViewModel model = ViewModelProviders.of(this).get(MainViewModel.class);

        MainView mainView = MainView.create(this);
        mainView.setCallback(new MainView.Callback() {
            @Override
            public void onAddSubscriptionClick() {
                router.toAddSubscription();
            }

        });
        setContentView(mainView.getContentView());

        model.isShowLoading().observe(this, mainView::showLoading);
        model.isShowAddSubscription().observe(this, mainView::showAddSubscription);

        model.init();
    }

    private interface Router {

        int REQUEST_CODE_ADD_SUBSCRIPTION = 1;

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
    }
}
