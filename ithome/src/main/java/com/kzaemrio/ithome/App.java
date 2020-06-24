package com.kzaemrio.ithome;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Actions.executeOnBackground(() -> AndroidThreeTen.init(this));
    }
}
