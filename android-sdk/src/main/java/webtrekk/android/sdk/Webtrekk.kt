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
import android.net.Uri
import android.view.View
import java.io.File
import webtrekk.android.sdk.core.WebtrekkImpl
import webtrekk.android.sdk.events.ActionEvent
import webtrekk.android.sdk.events.MediaEvent
import webtrekk.android.sdk.events.PageViewEvent

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
 *  .requestsInterval(TimeUnit.MINUTES, 15)
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
 *  .requestsInterval(TimeUnit.MINUTES, 15)
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
    abstract fun trackCustomPage(pageName: String, trackingParams: Map<String, String> = emptyMap())

    /**
     * Tracks a specific custom event, with a custom tracking params.
     *
     * @param eventName the event name that will be used for tracking that specific event.
     * @param trackingParams the custom tracking params that are associated with this tracking event.
     *  @see [Param] and [TrackingParams] for setting up the custom params.
     *
     * @throws [IllegalStateException] if [Config] config is not initialized.
     */
    abstract fun trackCustomEvent(
        eventName: String,
        trackingParams: Map<String, String> = emptyMap()
    )

    /**
     * Tracks a specific media event, witch custom media param and all other params.
     * @param mediaName the media name is name of the media file.
     * @param trackingParams the custom tracking params that are associated with media event and tracking event.
     *  @see [MediaParam], [Param] and [TrackingParams] for setting up the custom params.
     *
     * @throws [IllegalStateException] if [Config] config is not initialized.
     */
    abstract fun trackMedia(
        mediaName: String,
        trackingParams: Map<String, String> = emptyMap()
    )

    abstract fun trackMedia(
        pageName: String,
        mediaName: String,
        trackingParams: Map<String, String> = emptyMap()
    )

    /**
     * Tracks specific exception event.
     * @param exception the exception object that caused crash
     * @param exceptionType to internally decide if exception is of type [ExceptionType.CAUGHT] or [ExceptionType.CUSTOM]
     * @throws [IllegalStateException] if [Config] config is not initialized.
     */
    internal abstract fun trackException(
        exception: Exception,
        exceptionType: ExceptionType
    )

    /**
     * Tracks a specific exception event, can be used for all types of the exceptions.
     * @param exception is used for handled exception.
     * @throws [IllegalStateException] if [Config] config is not initialized.
     */
    abstract fun trackException(
        exception: Exception
    )

    /**
     * Tracks a custom exception can be used for testing or send some critical exception.
     * @param name custom name of the exception.
     * @param message custom message can be exception stacktrace or message.
     * @throws [IllegalStateException] if [Config] config is not initialized.
     */
    abstract fun trackException(
        name: String,
        message: String
    )

    /**
     * Tracks uncaught exception.
     * @param file file where uncaught exception handler saved throwable that caused app to crash
     * @throws [IllegalStateException] if [Config] config is not initialized.
     */
    internal abstract fun trackException(
        file: File
    )

    /**
     * Tracks a specific form events, in background we will take all import value from the view.
     * @param context context of the current activity.
     * @param view *optional* overrides the local view used in activity
     * @param formTrackingSettings *optional* contain additional settings for the form tracking
     *
     */
    abstract fun formTracking(
        context: Context,
        view: View? = null,
        formTrackingSettings: FormTrackingSettings = FormTrackingSettings()
    )

    /**
     * Method which will track campaign parameters from url
     * @param url url object with campaign parameters
     * @param mediaCode *optional* mediaCode - custom media code, if nil "wt_mc" is used as default
     */

    abstract fun trackUrl(
        url: Uri,
        mediaCode: String? = null
    )

    abstract fun trackPage(
        page: PageViewEvent
    )

    abstract fun trackAction(
        action: ActionEvent
    )

    abstract fun trackMedia(
        media: MediaEvent
    )

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
    abstract fun getEverId(): String?

    /**
     * This function can be used to change trackingId and Domain in one session
     * If need to keep same settings engage function must be changed
     *
     * @param * The [trackIds] that you get in your Webtrekk's account. [trackIds] must be set in the configuration,
     * otherwise webtrekk won't send any tracking data.
     * @param [trackDomain] domain that all the analytics data will be sent to. Make sure it's a valid domain.
     * @throws [IllegalStateException] if [Context] context is not initialized.
     */
    abstract fun setIdsAndDomain(trackIds: List<String>, trackDomain: String)

    abstract fun getTrackIds(): List<String>

    abstract fun getTrackDomain(): String

    /**
     * Returns [String] the user Agent which is associated per each unique device.
     *
     * @throws [IllegalStateException] if [Context] context is not initialized.
     */
    abstract fun getUserAgent(): String

    /**
     * Due to GDPR and other privacy regulations, someone might want to track without user recognition.
     * Limitations: Enabling this option will significantly decrease data quality! It is only recommended if no other option exists to comply to data privacy regulations
     *
     * @param enabled set it to true we will disable user recognition, and to false it will enable.
     * @param suppressParams this can disable any other parameter alongside the everID.
     *
     * @throws [IllegalStateException] if [Context] context is not initialized.
     */
    abstract fun anonymousTracking(
        enabled: Boolean = true,
        suppressParams: Set<String> = emptySet(),
    )

    abstract fun isAnonymousTracking():Boolean

    /**
     * Start immediate request sending and cleaning after requests are successfully sent
     */
    abstract fun sendRequestsNowAndClean()

    abstract fun startPeriodicWorkRequest()

    /**
     * Check if batch is enabled in the Webtrekk  configuration
     */
    abstract fun isBatchEnabled(): Boolean

    /**
     * Set batch enabled or disabled after initialization
     */
    abstract fun setBatchEnabled(enabled: Boolean)

    /**
     * Get number of track records sent in one batch
     */
    abstract fun getRequestsPerBatch(): Int

    /**
     * Set count of track records to be sent in one batch
     */
    abstract fun setRequestPerBatch(requestsCount: Int)

    /**
     * Returns DMC User ID if exist. DmcUserId is unique id generated from MAPP Engage SDK.
     * It provides user matching between these two SDKs
     */
    abstract fun getDmcUserId(): String?

    /**
     * Get exception tracking mode
     */
    abstract fun getExceptionLogLevel(): ExceptionType

    /**
     * Set exception tracking mode
     */
    abstract fun setExceptionLogLevel(exceptionType: ExceptionType)

    /**
     * Reset SDK configuration, clean all saved settings
     */
    internal abstract fun clearSdkConfig()

    /**
     * Reset everId in runtime, after initialization.
     *
     * If null or empty string provided, new everId will be generated and set
     */
    abstract fun setEverId(everId: String?)

    /**
     * Enable or disable sending app version in every request
     */
    abstract fun setVersionInEachRequest(enabled: Boolean)

    /**
     * Check if app version is set to be sent in every request or not
     */
    abstract fun getVersionInEachRequest(): Boolean

    /**
     * Set logging level for console printing
     */
    abstract fun setLogLevel(logLevel: Logger.Level)

    /**
     * Set interval for automatic data sending
     */
    abstract fun setRequestInterval(minutes: Long)

    /**
     * Returns if user matching is enabled or disabled
     */
    abstract fun isUserMatchingEnabled():Boolean

    /**
     * Enable or disable user matching between Engage and Intelligence system
     */
    abstract fun setUserMatchingEnabled(enabled: Boolean)

    /**
     * Check if SDK is initialized
     */
    abstract fun isInitialized(): Boolean

    /**
     * Get configuration parameter that are valid in the method calling time
     */
    abstract fun getCurrentConfiguration():ActiveConfig

    /**
     * Set temporary userId used to track user during a single session.
     * It can be used only when anonymous tracking is enabled.
     * Setting this value has no effect when anonymous tracking is disabled.
     */
    abstract fun setTemporaryUserId(userId:String?)

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

        /**
         * Resets all configuration for SDK. After this call client must initialize SDK with new configuration.
         */
        @JvmStatic
        fun reset(context: Context) {
            WebtrekkImpl.reset(context)
        }
    }
}
