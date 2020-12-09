package com.kzaemrio.ithome;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends ComponentActivity {

    @Inject
    MainView mView;

    @Inject
    WebHelper mWebHelper;

    MainViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mView.init();
        mView.setCallback(new MainView.Callback() {
            @Override
            public void onRefresh() {
                mViewModel.load();
            }

            @Override
            public void onItemClick(ItemListAdapter.ViewItem item) {
                startActivity(DetailActivity.createIntent(
                        MainActivity.this,
                        item.getItem().getLink(),
                        item.getItem().getTitle())
                );
            }

            @Override
            public void onItemLongClick(ItemListAdapter.ViewItem item) {
                mWebHelper.shareItem(MainActivity.this, item.getItem());
            }
        });
        setContentView(mView.getContentView());

        mViewModel.getIsShowLoading().observe(this, mView::showIsLoading);
        mViewModel.getItemList().observe(this, mView::bind);
        mViewModel.getItemList().observe(this, list -> invalidateOptionsMenu());
        mViewModel.getScrollPosition().observe(this, mView::scrollToPositionWithOffset);

        mViewModel.load();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mView.hasData()) {
            mViewModel.saveItemPosition(
                    mView.getFirstPosition(),
                    mView.getOffset()
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_cache).setVisible(mView.hasData());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_cache) {
            mView.showCacheDialog(mViewModel.getItemList().getValue());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
