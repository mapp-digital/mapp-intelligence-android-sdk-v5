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

import androidx.work.Constraints
import okhttp3.OkHttpClient
import webtrekk.android.sdk.extension.nullOrEmptyThrowError
import webtrekk.android.sdk.extension.validateEntireList
import java.util.concurrent.TimeUnit

/**
 * The entry point where you can set up all the configurations that will be used by the lib.
 *
 * A concrete implementation of [Config] interface.
 * You must use this class with [Webtrekk.init] before using any other method in [Webtrekk].
 *
 * A sample usage:
 * val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds = listOf("123", "456"), trackDomain = "https://www.webtrekk.com")
 *      .logLevel(Logger.Level.BASIC)
 *      .sendDelay(TimeUnit.HOURS, 12)
 *      .disableAutoTracking()
 *      .build()
 *
 */
class WebtrekkConfiguration private constructor(
    override val trackIds: List<String>,
    override val trackDomain: String,
    override val logLevel: Logger.Level,
    override val sendDelay: Long,
    override val autoTracking: Boolean,
    override val workManagerConstraints: Constraints,
    override val okHttpClient: OkHttpClient
) : Config {

    /**
     * [trackIds] and [trackDomain] must be set in [Builder] constructor. Make sure that they are valid,
     * otherwise, the tracking data will be sent to the wrong domain.
     */
    class Builder(private val trackIds: List<String>, private val trackDomain: String) {
        private var logLevel = DefaultConfiguration.LOG_LEVEL_VALUE
        private var sendDelay =
            DefaultConfiguration.TIME_UNIT_VALUE.toMillis(DefaultConfiguration.SEND_DELAY_VALUE)
        private var autoTracking = DefaultConfiguration.AUTO_TRACK_ENABLED
        private var constraints = DefaultConfiguration.WORK_MANAGER_CONSTRAINTS
        private var okHttpClientBuilder = DefaultConfiguration.OKHTTP_CLIENT

        /**
         * Configure the log level of the lib.
         *
         * To stop all logs in the lib, use {@code logLevel(Logger.Level.NONE)}.
         *
         * @see [DefaultConfiguration.LOG_LEVEL_VALUE] for the default.
         */
        @JvmName("setLogLevel")
        fun logLevel(logLevel: Logger.Level) = apply { this.logLevel = logLevel }

        /**
         * Set when should the cached tracking data be sent to the server in interval time.
         *
         * [Webtrekk] uses [androidx.work.WorkManager] for enqueueing and sending the requests in [sendDelay] time
         * in the background in order. It guarantees to send those data requests in periodic time even if your app is not in the background,
         * and that's for enhancing your app's usage battery and that you don't have to worry about the performance.
         *
         * *NOTE* the minimum interval period is 15 minutes.
         *
         * @see [DefaultConfiguration.SEND_DELAY_VALUE] for the default value.
         */
        @JvmName("setSendDelay")
        fun sendDelay(timeUnit: TimeUnit = TimeUnit.MINUTES, sendDelay: Long) =
            apply { this.sendDelay = timeUnit.toMillis(sendDelay) }

        /**
         * The auto tracking is enabled by default. The auto tracking will track your activities
         * and fragments life cycles.
         *
         * *NOTE* if auto tracking is enabled, then you are not allowed to use the manual tracking [Webtrekk.trackPage]
         *
         * Call [disableAutoTracking] to disable the auto tracking.
         */
        fun disableAutoTracking() = apply { this.autoTracking = false }

        /**
         * Customize when should the lib send the tracking requests to the server when some device constraints
         * are met.
         *
         * @see [androidx.work.Constraints] Once the constraints are met, within [sendDelay] time,
         * then the lib will start sending the requests to the server by using [androidx.work.WorkManager] worker.
         *
         * A sample usage:
         * val constraints = Constraints.Builder()
         *      .setRequiredNetworkType(NetworkType.CONNECTED)
         *      .setRequiresCharging(true)
         *      .setRequiresBatteryNotLow(true)
         *      .build()
         *
         * @see [DefaultConfiguration.WORK_MANAGER_CONSTRAINTS] the default constraints.
         */
        @JvmName("setWorkManagerConstraints")
        fun workManagerConstraints(constraints: Constraints) =
            apply { this.constraints = constraints }

        /**
         * Configure the [okHttpClient] that the lib uses to send the requests over network.
         *
         * Use the builder methods to configure the derived client for a specific purpose.
         *
         * A sample usage:
         * val okHttpClient = OkHttpClient.Builder()
         *      .readTimeout(10, TimeUnit.SECONDS)
         *      .addNetworkInterceptor(StethoInterceptor())
         *      .certificatePinner("your certificates")
         *      .build()
         *
         * @see [DefaultConfiguration.OKHTTP_CLIENT] the defailt okHttpClient configurations.
         */
        @JvmName("setOkHttpClient")
        fun okHttpClient(okHttpClient: OkHttpClient) =
            apply { this.okHttpClientBuilder = okHttpClient }

        /**
         * Returns an instance of [WebtrekkConfiguration] with configurations that are set in the [Builder]
         */
        fun build() = WebtrekkConfiguration(
            trackIds.validateEntireList("trackIds"),
            trackDomain.nullOrEmptyThrowError("trackDomain"),
            logLevel,
            sendDelay,
            autoTracking,
            constraints,
            okHttpClientBuilder
        )
    }
}
