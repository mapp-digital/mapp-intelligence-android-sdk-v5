package com.example.webtrekk.samplejava;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import webtrekk.android.sdk.Webtrekk;
import webtrekk.android.sdk.WebtrekkConfiguration;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        List<String> ids = new ArrayList<>();
        ids.add("1");

        WebtrekkConfiguration webtrekkConfiguration = new WebtrekkConfiguration.Builder(ids, "www.webtrekk.com")
                .build();

        Webtrekk.getInstance().init(this, webtrekkConfiguration);
    }
}
