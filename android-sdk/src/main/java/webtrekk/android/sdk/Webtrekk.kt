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

package webtrekk.android.sdk

import android.content.Context
import webtrekk.android.sdk.core.WebtrekkImpl

/**
 * Webtrekk is a library used to collect your app usage, how your customers interacting with your app,
 * tracking specific pages and custom events. And send those data to Webtrekk analytics to be used
 * for further analysis.
 *
 * Abstract class and the entry point to start using [Webtrekk] trackers.
 *
 * Webtrekk internally, collects and caches the data that you specify for tracking, and later, it sends
 * those data to Webtrekk analytics in periodic time.
 *
 * As a matter of performance, Webtrekk uses [kotlinx.coroutines] for caching, collecting and sending
 * the data in the background (pool of threads). Hence, Webtrekk doesn't block the main thread.
 *
 * Webtrekk uses [androidx.work.WorkManager] for enqueueing the requests (cached data) and sending them in [Config.requestsInterval]
 * time in the background in order. It guarantees to send those data requests in periodic time even if your app is not in the background,
 * and that's for enhancing your app's usage battery and that you don't have to worry about the performance.
 *
 * You can configure the work manager constraints [Config.workManagerConstraints] like sending the requests if
 * the device is charging or when battery is not low. Check [DefaultConfiguration.WORK_MANAGER_CONSTRAINTS]
 * for the default constraints in case of you not adding your own constraints.
 *
 * To start using [Webtrekk], you must retrieve an instance first and then initialize the context [Context]
 * and the configurations [Config]. Without specifying the context nor the configurations first, webtrekk
 * will throw [IllegalStateException] upon invoking any method.
 *
 * A sample usage:
 *
 * val webtrekkConfigurations = WebtrekkConfiguration.Builder(trackIds = listOf("1234567"), trackDomain = "https://www.webtrekk.com")
 *  .logLevel(Logger.Level.BASIC)
 *  .requestsInterval(TimeUnit.HOURS, 10)
 *  .build()
 *
 * val webtrekk = Webtrekk.getInstance()
 * webtrekk.init(this, webtrekkConfigurations)
 *
 * *NOTE* auto tracking is enabled by default, so once you put this code in your [android.app.Application] class,
 * you will start receiving your app activities and fragments tracking data, getting cached, and will be sent to
 * Webtrekk analytics in [Config.requestsInterval] time.
 *
 * Since webtrekk internally uses [okhttp3.OkHttpClient] for networking, you could configure [okhttp3.OkHttpClient]
 * through the configurations. Hence, you could add your own certificate, interceptor and so on.
 * @see [DefaultConfiguration.OKHTTP_CLIENT] for the default OkHttpClient that is used by Webtrekk.
 *
 * A sample usage:
 *
 * val okHttpClientConfig = OkHttpClient.Builder()
 *  .readTimeout(15, TimeUnit.SECONDS)
 *  .addNetworkInterceptor(StethoInterceptor())
 *  .build()
 *
 * val webtrekkConfigurations = WebtrekkConfiguration.Builder(trackIds = listOf("1234567"), trackDomain = "https://www.webtrekk.com")
 *  .logLevel(Logger.Level.BASIC)
 *  .requestsInterval(TimeUnit.HOURS, 10)
 *  .okHttpClient(okHttpClient = okHttpClientConfig)
 *  .build()
 *
 * *NOTE* make sure your app's manifest has the network permission
 *  <uses-permission android:name="android.permission.INTERNET"/>
 *
 */
abstract class Webtrekk protected constructor() {

    /**
     * Initializes the [Context.getApplicationContext] context and [Config] Webtrekk's configurations
     * that will be used through the entire life of [Webtrekk] object.
     *
     * *NOTE* this method must be called once and first before invoking any other method. Invoking any other method
     * without initializing the context nor the configurations, will throw [IllegalStateException].
     *
     * It's preferred to initialize in the [android.app.Application].
     *
     * @param context object for the configurations. This class will call [Context.getApplicationContext]
     *   so it's safely to pass any [Context] without risking a memory leak.
     * @param config an interface that is used to set up Webtrekk configurations. Use [WebtrekkConfiguration]
     *   which is a concrete implementation of [Config] where you can set up all your configurations.
     */
    abstract fun init(context: Context, config: Config)

    /**
     * Used as a manual tracking in case of disabling the auto tracking in [Config.autoTracking].
     * Use this method to track a specific page [android.app.Activity] with optional overriding
     * the local class name and optional some extra tracking params.
     *
     * If auto tracking [Config.autoTracking] is enabled, then this function will return
     * immediately without sending its tracking data, alongside with a warning indicating that
     * auto tracking is enabled.
     *
     * @param context context of the current activity.
     * @param customPageName *optional* overrides the local class name of the activity. If set to null, then
     *   the local class name of the activity in [context] will be used instead.
     * @param trackingParams *optional* the custom tracking params that are associated with the current page.
     *   @see [Param] and [TrackingParams] for defining the custom params.
     *
     * @throws [IllegalStateException] if [Config] config is not initialized.
     */
    abstract fun trackPage(
        context: Context,
        customPageName: String? = null,
        trackingParams: Map<String, String> = emptyMap()
    )

    /**
     * Tracks a custom tracking page, with a custom tracking params.
     * Could be used alongside the auto tracking.
     *
     * @param pageName the custom page name that will be used for tracking.
     * @param trackingParams the custom tracking params that are associated with the custom tracking
     *   page. @see [Param] and [TrackingParams] for setting up the custom params.
     *
     * @throws [IllegalStateException] if [Config] config is not initialized.
     */
    abstract fun trackCustomPage(pageName: String, trackingParams: Map<String, String>)

    /**
     * Tracks a specific custom event, with a custom tracking params.
     * Could be used alongside the auto tracking.
     *
     * @param eventName the event name that will be used for tracking that specific event.
     * @param trackingParams the custom tracking params that are associated with this tracking event.
     *  @see [Param] and [TrackingParams] for setting up the custom params.
     *
     * @throws [IllegalStateException] if [Config] config is not initialized.
     */
    abstract fun trackCustomEvent(eventName: String, trackingParams: Map<String, String>)

    /**
     * Allows to opt out entirely from tracking. Internally, calling this method will cause to delete
     * all the current tracking data that are cached in the database (if sendCurrentData is false),
     * canceling sending requests, shutting down work manager's worker and disabling all incoming tracking requests.
     *
     * To stop opting out, you have to set [optOut(false)].
     *
     * In case there are some data already in the cache, and it's desirable to send them before
     * deleting all the data, set sendCurrentData to true, this allows to send first all current
     * requests in the cache, then delete everything.
     *
     * @param value set it to true to opt out, false to cancel.
     * @param sendCurrentData set it to true, to send current data in cache before deleting them.
     *
     * @throws [IllegalStateException] if [Context] context is not initialized.
     */
    abstract fun optOut(value: Boolean, sendCurrentData: Boolean = false)

    /**
     * Returns [Boolean] true if the user is opt'd-out, false otherwise.
     *
     * @throws [IllegalStateException] if [Context] context is not initialized.
     */
    abstract fun hasOptOut(): Boolean

    /**
     * Returns [String] the user ever id which is associated per each unique user.
     *
     * @throws [IllegalStateException] if [Context] context is not initialized.
     */
    abstract fun getEverId(): String

    companion object {

        /**
         * Retrieves a singleton instance of [Webtrekk].
         *
         * To start integrating with [Webtrekk] you must get instance of Webtrekk first.
         */
        @JvmStatic
        fun getInstance(): Webtrekk {
            return WebtrekkImpl.getInstance()
        }
    }
}
