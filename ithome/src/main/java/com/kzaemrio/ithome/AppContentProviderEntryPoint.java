package com.kzaemrio.ithome;

import com.kzaemrio.ithome.db.AppDataBase;

import java.util.concurrent.ExecutorService;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@EntryPoint
@InstallIn(ApplicationComponent.class)
public interface AppContentProviderEntryPoint {
    ExecutorService executorService();

    ListHelper listHelper();

    Rss rss();

    AppDataBase appDataBase();
}
