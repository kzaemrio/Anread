package com.kzaemrio.ithome;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.kzaemrio.ithome.event.OnHasNewEvent;
import com.kzaemrio.ithome.event.OnHideWebViewLoadingEvent;
import com.kzaemrio.ithome.event.OnItemListEvent;
import com.kzaemrio.ithome.event.OnRefreshHideEvent;
import com.kzaemrio.ithome.event.OnScrollToPositionWithOffsetEvent;
import com.kzaemrio.ithome.event.OnShowWebViewLoadingEvent;
import com.kzaemrio.ithome.event.OnWebViewHideStartEvent;
import com.kzaemrio.ithome.event.OnWebViewShowEndEvent;
import com.kzaemrio.simplebus.lib.Bus;
import com.kzaemrio.simplebus.lib.SimpleBus;
import com.kzaemrio.simplebus.lib.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private MainView mView;

    private PreferencesWrapper mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = new PreferencesWrapper(getPreferences(MODE_PRIVATE));

        mView = new MainView(this);
        mView.setCallback(this::requestData);
        setContentView(mView.getContentView());
        requestData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SimpleBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mView.isInit()) {
            mPreferences.save(mView.getPubDate(), mView.getOffset());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SimpleBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (mView.isWebViewShow()) {
            mView.hideWebView();
        } else {
            mView.destroyWebView();
            super.onBackPressed();
        }
    }

    private void requestData() {
        mView.showRefresh();

        boolean init = mView.isInit();
        int offset = mPreferences.getOffset();
        long pubDate = mPreferences.getPubDate();

        Actions.executeOnBackground(RequestFactory.create(init, offset, pubDate));
    }

    @Subscribe
    public void onEvent(OnRefreshHideEvent event) {
        mView.hideRefresh();
    }

    @Subscribe
    public void onEvent(OnItemListEvent event) {
        mView.bind(event.getList());
    }

    @Subscribe
    public void onEvent(OnScrollToPositionWithOffsetEvent event) {
        mView.scrollToPositionWithOffset(event.getIndex(), event.getOffset());
    }

    @Subscribe
    public void onEvent(OnHasNewEvent event) {
        mView.alertHasNew();
    }

    @Subscribe
    public void OnEvent(OnShowWebViewLoadingEvent event) {
        mView.showWebViewLoading();
    }

    @Subscribe
    public void OnEvent(OnHideWebViewLoadingEvent event) {
        mView.hideWebViewLoading();
    }

    @Subscribe
    public void onEvent(OnWebViewShowEndEvent event) {
        mView.hideList();
    }

    @Subscribe
    public void onEvent(OnWebViewHideStartEvent event) {
        mView.showList();
    }

    public static class PreferencesWrapper {

        private static final String KEY_PUB_DATE = "KEY_PUB_DATE";
        private static final String KEY_OFFSET = "KEY_OFFSET";

        private final SharedPreferences mPreferences;

        public PreferencesWrapper(SharedPreferences preferences) {
            mPreferences = preferences;
        }

        public void save(long time, int offset) {
            mPreferences.edit()
                    .putLong(KEY_PUB_DATE, time)
                    .putInt(KEY_OFFSET, offset)
                    .apply();
        }

        public long getPubDate() {
            return mPreferences.getLong(KEY_PUB_DATE, 0);
        }

        public int getOffset() {
            return mPreferences.getInt(KEY_OFFSET, 0);
        }
    }

    private static class RequestFactory {
        public static Runnable create(boolean init, int offset, long pubDate) {
            return () -> {
                Bus bus = SimpleBus.getDefault();

                List<Item> itemList = Actions.requestItemList();
                List<ItemListAdapter.ViewItem> list = new ArrayList<>(itemList.size());
                for (Item item : itemList) {
                    ItemListAdapter.ViewItem viewItem = ItemListAdapter.ViewItem.create(item);
                    list.add(viewItem);
                }

                int index;
                if (!init) {
                    index = Actions.binarySearch(list, pubDate, it -> it.getItem().getPubDate());
                } else {
                    index = -1;
                }

                bus.post(new OnItemListEvent(list));

                if (index >= 0) {
                    bus.post(new OnScrollToPositionWithOffsetEvent(index, offset));

                    if (index > 0) {
                        bus.post(new OnHasNewEvent());
                    }
                }
                bus.post(new OnRefreshHideEvent());
            };
        }
    }
}
