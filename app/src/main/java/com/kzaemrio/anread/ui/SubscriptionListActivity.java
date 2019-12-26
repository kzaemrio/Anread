package com.kzaemrio.anread.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.adapter.SimpleDividerItemDecoration;
import com.kzaemrio.anread.adapter.SubscriptionAdapter;
import com.kzaemrio.anread.model.Subscription;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionListActivity extends AppCompatActivity {

    private SubscriptionListViewModel mViewModel;

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, SubscriptionListActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SubscriptionListViewModel.class);

        MvpView view = MvpView.create(this, subscription -> {
            setResult(RESULT_OK);
            mViewModel.delete(subscription);
        });
        setContentView(view.getContentView());

        mViewModel.getData().observe(this, view::bind);
        mViewModel.loadSubscriptionList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscription, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Router.to(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Router.REQUEST_ADD_SUBSCRIPTION && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            mViewModel.loadSubscriptionList();
        }
    }

    private interface Router {
        int REQUEST_ADD_SUBSCRIPTION = 1;

        static void to(Activity activity) {
            activity.startActivityForResult(
                    AddSubscriptionActivity.createIntent(activity),
                    REQUEST_ADD_SUBSCRIPTION
            );
        }
    }

    private interface MvpView {
        static MvpView create(Context context, Consumer<Subscription> consumer) {

            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));

            return new MvpView() {
                @Override
                public View getContentView() {
                    return recyclerView;
                }

                @Override
                public void bind(List<Subscription> subscriptions) {
                    recyclerView.setAdapter(new SubscriptionAdapter(subscriptions, this::showDeleteDialog));
                }

                private void showDeleteDialog(Subscription subscription) {
                    new AlertDialog.Builder(context)
                            .setTitle(subscription.getTitle())
                            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    consumer.accept(subscription);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                }
            };
        }

        View getContentView();

        void bind(List<Subscription> subscriptions);
    }
}
