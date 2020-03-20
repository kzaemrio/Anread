package com.kzaemrio.anread;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Actions.executeOnDiskIO(() -> AndroidThreeTen.init(this));
    }
}
