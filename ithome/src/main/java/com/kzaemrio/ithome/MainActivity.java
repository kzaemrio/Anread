package com.kzaemrio.ithome;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    protected void onPause() {
        super.onPause();
        if (mView.isInit()) {
            mPreferences.save(mView.getPubDate(), mView.getOffset());
        }
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
        Actions.executeOnBackground(() -> {
            runOnUiThread(mView::showRefresh);

            List<ItemListAdapter.ViewItem> list = Actions.requestItemList().stream()
                    .filter(item -> !item.getTitle().contains("IT之家"))
                    .sorted(Comparator.comparing(Item::getPubDate).reversed())
                    .map(ItemListAdapter.ViewItem::create)
                    .collect(Collectors.toList());

            int offset;
            int index;
            if (!mView.isInit()) {
                long pubDate = mPreferences.getPubDate();
                offset = mPreferences.getOffset();
                index = Actions.binarySearch(list, pubDate, it -> it.getItem().getPubDate());
            } else {
                index = -1;
                offset = 0;
            }

            runOnUiThread(() -> {
                mView.bind(list);

                if (index >= 0) {
                    mView.scrollToPositionWithOffset(index, offset);

                    if (index > 0) {
                        mView.alertHasNew();
                    }
                }
                mView.hideRefresh();
            });
        });
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
}
