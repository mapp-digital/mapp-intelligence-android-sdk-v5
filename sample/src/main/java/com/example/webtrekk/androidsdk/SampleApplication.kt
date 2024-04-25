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

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()

        val stringIds = BuildConfig.TRACK_IDS
        val domain = BuildConfig.DOMEIN
        val trackIds: List<String> = stringIds.split(",")

        val webtrekkConfigurations =
            WebtrekkConfiguration.Builder(trackIds, domain)
                .setEverId("2222")
                //.disableAutoTracking()
                .logLevel(Logger.Level.BASIC)
                .disableAutoTracking()
                .requestsInterval(TimeUnit.MINUTES, 1)
                .sendAppVersionInEveryRequest(true)
                .okHttpClient(okHttpClient)
                .enableMigration()
                .enableCrashTracking(ExceptionType.ALL)
                .workManagerConstraints(constraints = constraints)
                .setBatchSupport(false)
                .setUserMatchingEnabled(true)
                .build()

        Webtrekk.getInstance().init(context = this, config = webtrekkConfigurations)

        Webtrekk.getInstance().apply {
            optOut(true,true)
            // anonymousTracking(false, emptySet())
            // setEverId(null)
            // setTemporarySessionId("user-xyz-123456789")
        }
        // EngageSdk.init(application = this)
    }
}
