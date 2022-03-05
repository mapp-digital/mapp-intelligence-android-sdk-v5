/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package com.example.webtrekk.androidsdk

// import com.facebook.stetho.Stetho
// import com.facebook.stetho.okhttp3.StethoInterceptor
import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import com.appoxee.Appoxee
import com.appoxee.AppoxeeOptions
import com.appoxee.push.NotificationMode
import java.util.concurrent.TimeUnit
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val opt = AppoxeeOptions()
        opt.sdkKey = "17ed4c40fff620.10688629"
        opt.googleProjectId = "785651527831"
        opt.cepURL = "https://jamie.g.shortest-route.com"
        opt.appID = "310437"
        opt.tenantID = "60211"
        opt.notificationMode = NotificationMode.BACKGROUND_AND_FOREGROUND
        opt.server = AppoxeeOptions.Server.EMC_US
        opt.logLevel=AppoxeeOptions.LogLevel.CLIENT_DEBUG
        Appoxee.engage(this, opt)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val stringIds = BuildConfig.TRACK_IDS
        val domain = BuildConfig.DOMEIN
        val elements: List<String> = stringIds.split(",")
        val sharedPref =
            this.getSharedPreferences("Sample Application", Context.MODE_PRIVATE) ?: return
        val webtrekkConfigurations =
            WebtrekkConfiguration.Builder(elements, domain)
                //.setEverId("11111")
                .disableAutoTracking()
                .logLevel(Logger.Level.BASIC)
                .requestsInterval(TimeUnit.MINUTES, 1)
                .sendAppVersionInEveryRequest()
                .enableCrashTracking(
                    ExceptionType.valueOf(
                        sharedPref.getString("ExceptionType", ExceptionType.ALL.toString())
                            ?: ExceptionType.ALL.toString()
                    )
                )
                .workManagerConstraints(constraints = constraints)
                .setBatchSupport(Prefs(this).isBatchEnabled)
                .build()

        Webtrekk.getInstance().init(this, webtrekkConfigurations)
    }
}
