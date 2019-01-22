package com.example.webtrekk.androidsdk

import android.app.Application
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import java.util.concurrent.TimeUnit

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val webtrekkConfigurations = WebtrekkConfiguration.Builder(listOf("1"), "www.webtrekk.com")
            .logLevel(Logger.Level.BASIC)
            .sendDelay(TimeUnit.MINUTES, 15)
            .build()

        Webtrekk.getInstance().init(this, webtrekkConfigurations)
    }
}
