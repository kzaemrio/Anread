package com.kzaemrio.ithome;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.greenrobot.eventbus.EventBus;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Actions.executeOnBackground(() -> {
            EventBus.builder().executorService(Actions.pool).addIndex(new EventBusIndex()).installDefaultEventBus();
            AndroidThreeTen.init(this);
        });
    }
}
