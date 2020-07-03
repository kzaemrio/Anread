package com.kzaemrio.ithome;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kzaemrio.simplebus.lib.SimpleBus;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Actions.executeOnBackground(() -> {
            SimpleBus.init(new AutoParams());
            AndroidThreeTen.init(this);
        });
    }
}
