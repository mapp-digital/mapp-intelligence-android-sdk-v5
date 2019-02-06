package com.example.webtrekk.androidsdk

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import java.util.concurrent.TimeUnit

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED).build()

        val webtrekkConfigurations = WebtrekkConfiguration.Builder(listOf("1"), "https://www.webtrekk.com")
            .logLevel(Logger.Level.BASIC)
            .sendDelay(TimeUnit.MINUTES, 15)
            .workManagerConstraints(constraints = constraints)
            .disableAutoTracking()
            .build()

        Webtrekk.getInstance().init(this, webtrekkConfigurations)
    }
}
