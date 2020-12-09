package com.kzaemrio.ithome;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@EntryPoint
@InstallIn(ApplicationComponent.class)
public interface AppContentProviderEntryPoint {
    BackgroundExecutor backgroundExecutor();

    ListHelper listHelper();

    Rss rss();
}
