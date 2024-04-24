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
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import webtrekk.android.sdk.data.model.GenerationMode
import webtrekk.android.sdk.extension.nullOrEmptyThrowError
import webtrekk.android.sdk.extension.validateEntireList
import webtrekk.android.sdk.util.webtrekkLogger

/**
 * The entry point where you can set up all the configurations that will be used by the lib.
 *
 * A concrete implementation of [Config] interface.
 * You must use this class with [Webtrekk.init] before using any other method in [Webtrekk].
 *
 * A sample usage:
 * val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds = listOf("123", "456"), trackDomain = "https://www.webtrekk.com")
 *      .logLevel(Logger.Level.BASIC)
 *      .requestsInterval(TimeUnit.HOURS, 12)
 *      .disableAutoTracking()
 *      .build()
 *
 */
data class WebtrekkConfiguration private constructor(
    override var trackIds: List<String>,
    override var trackDomain: String,
    override val logLevel: Logger.Level,
    override var requestsInterval: Long,
    override val autoTracking: Boolean,
    override val fragmentsAutoTracking: Boolean,
    override val workManagerConstraints: Constraints,
    override val okHttpClient: OkHttpClient,
    override var requestPerBatch: Int,
    override var batchSupport: Boolean,
    override val activityAutoTracking: Boolean,
    override var exceptionLogLevel: ExceptionType,
    override val shouldMigrate: Boolean,
    override var versionInEachRequest: Boolean,
    override var everId: String?,
    override var userMatchingEnabled: Boolean,
    override var everIdMode: GenerationMode?,
) : Config {

    /**
     * [trackIds] and [trackDomain] must be set in [Builder] constructor. Make sure that they are valid,
     * otherwise, the tracking data will be sent to the wrong domain.
     */
    class Builder(private val trackIds: List<String>, private val trackDomain: String) {
        private var logLevel = DefaultConfiguration.LOG_LEVEL_VALUE
        private var requestsInterval =
            DefaultConfiguration.TIME_UNIT_VALUE.toMinutes(DefaultConfiguration.REQUESTS_INTERVAL)
        private var autoTracking = DefaultConfiguration.AUTO_TRACK_ENABLED
        private var fragmentsAutoTracking = DefaultConfiguration.FRAGMENTS_AUTO_TRACK_ENABLED
        private var constraints = DefaultConfiguration.WORK_MANAGER_CONSTRAINTS
        private var okHttpClientBuilder = DefaultConfiguration.OKHTTP_CLIENT
        private var requestPerBatch = DefaultConfiguration.REQUEST_PER_BATCH
        private var batchSupport = DefaultConfiguration.BATCH_SUPPORT_ENABLED
        private var activityAutoTracking = DefaultConfiguration.ACTIVITY_AUTO_TRACK_ENABLED
        private var exceptionLogLevel = DefaultConfiguration.CRASH_TRACKING_ENABLED
        private var shouldMigrate = DefaultConfiguration.SHOULD_MIGRATE_ENABLED
        private var versionInEachRequest = DefaultConfiguration.VERSION_IN_EACH_REQUEST
        private var everId: String? = ""
        private var userMatchingEnabled: Boolean = DefaultConfiguration.USER_MATCHING_ENABLED
        private var everIdMode: GenerationMode? = GenerationMode.AUTO_GENERATED


        fun setEverId(everId: String?) = apply {
            this.everId = everId
            this.everIdMode = GenerationMode.USER_GENERATED
        }

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
         * [Webtrekk] uses [androidx.work.WorkManager] for enqueueing and sending the requests in [requestsInterval] time
         * in the background in order. It guarantees to send those data requests in periodic time even if your app is not in the background,
         * and that's for enhancing your app's usage battery and that you don't have to worry about the performance.
         *
         * *NOTE* the minimum interval period is 15 minutes.
         *
         * @see [DefaultConfiguration.REQUESTS_INTERVAL] for the default value.
         */
        @JvmName("setRequestsInterval")
        fun requestsInterval(timeUnit: TimeUnit = TimeUnit.MINUTES, interval: Long) =
            apply {
                this.requestsInterval = timeUnit.toMinutes(interval)
            }

        /**
         * The auto tracking is enabled by default. The auto tracking will track your activities
         * and fragments life cycles.
         *
         * *NOTE* if auto tracking is enabled, then you are not allowed to use the manual tracking [Webtrekk.trackPage]
         *
         * Call [disableAutoTracking] to disable the auto tracking.
         */
        fun disableAutoTracking() = apply {
            this.autoTracking = false
            this.fragmentsAutoTracking = false
            this.activityAutoTracking = false
        }

        /**
         * The crash tracking is disabled by default. The crash tracking will track different exception types defined in [ExceptionType].
         * To enable it, call [enableCrashTracking] supplying different values of [ExceptionType] as arguments, except [ExceptionType.NONE]
         */
        fun enableCrashTracking(exceptionLogLevel: ExceptionType) = apply {
            this.exceptionLogLevel = exceptionLogLevel
        }

        /**
         * When auto tracking is enabled, the auto tracking of fragments is enabled by default.
         *
         * Call [disableActivityAutoTracking] to disable the auto tracking of Activity, and only auto track fragments.
         *
         * *NOTE* if auto track is disabled [disableAutoTracking], then auto tracking of fragments is disabled as well.
         */
        fun disableActivityAutoTracking() = apply {
            this.activityAutoTracking = false
        }

        /**
         * Migration of ever id and one parameter values from sdk version 4 is disabled by default. To enable it, call [enableMigration].
         */
        fun enableMigration() = apply {
            this.shouldMigrate = true
        }

        /**
         * If disabled, it will only be send if the SDK detects a new session.
         * It is recommended to enable when using the pre-defined app version parameter in order to be inline with the sessions as detected by the Mapp Intelligence backend.
         */

        fun sendAppVersionInEveryRequest(enabled: Boolean = true) = apply {
            this.versionInEachRequest = enabled
        }

        fun isUserMatchingEnabled(): Boolean {
            return this.userMatchingEnabled
        }

        fun setUserMatchingEnabled(enabled: Boolean) = apply {
            this.userMatchingEnabled = enabled
        }

        @JvmOverloads
        fun setBatchSupport(
            batchEnable: Boolean,
            requestsInBatch: Int = DefaultConfiguration.REQUEST_PER_BATCH
        ) = apply {
            this.batchSupport = batchEnable
            this.requestPerBatch = requestsInBatch
        }

        /**
         * When auto tracking is enabled, the auto tracking of fragments is enabled by default.
         *
         * Call [disableFragmentsAutoTracking] to disable the auto tracking of fragments, and only auto track activities.
         *
         * *NOTE* if auto track is disabled [disableAutoTracking], then auto tracking of fragments is disabled as well.
         */
        fun disableFragmentsAutoTracking() = apply { this.fragmentsAutoTracking = false }

        /**
         * Customize when should the lib send the tracking requests to the server when some device constraints
         * are met.
         *
         * @see [androidx.work.Constraints] Once the constraints are met, within [requestsInterval] time,
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
            trackIds = trackIds.validateEntireList("trackIds"),
            trackDomain = trackDomain.nullOrEmptyThrowError("trackDomain"),
            logLevel = logLevel,
            requestsInterval = requestsInterval,
            autoTracking = autoTracking,
            fragmentsAutoTracking = fragmentsAutoTracking,
            workManagerConstraints = constraints,
            okHttpClient = okHttpClientBuilder,
            requestPerBatch = requestPerBatch,
            batchSupport = batchSupport,
            activityAutoTracking = activityAutoTracking,
            exceptionLogLevel = exceptionLogLevel,
            shouldMigrate = shouldMigrate,
            versionInEachRequest = versionInEachRequest,
            everId = everId,
            userMatchingEnabled = userMatchingEnabled,
            everIdMode = everIdMode,
        ).also {
            setUserMatchingEnabled(userMatchingEnabled)
            setEverId(everId)
            it.everIdMode=everIdMode
        }
    }

    override fun copy(): Config {
        return WebtrekkConfiguration(
            trackIds = this.trackIds,
            trackDomain = this.trackDomain,
            logLevel = this.logLevel,
            requestsInterval = this.requestsInterval,
            autoTracking = this.autoTracking,
            fragmentsAutoTracking = this.fragmentsAutoTracking,
            workManagerConstraints = this.workManagerConstraints,
            okHttpClient = this.okHttpClient,
            requestPerBatch = this.requestPerBatch,
            batchSupport = this.batchSupport,
            activityAutoTracking = this.activityAutoTracking,
            exceptionLogLevel = this.exceptionLogLevel,
            shouldMigrate = this.shouldMigrate,
            versionInEachRequest = this.versionInEachRequest,
            everId = this.everId,
            userMatchingEnabled = this.userMatchingEnabled,
            everIdMode = this.everIdMode,
        )
    }

    override fun toJson(): String {
        try {
            val jsonArr = JSONArray()
            trackIds.forEach { jsonArr.put(it) }
            val jsonObject = JSONObject("{}")
            jsonObject.put("trackDomain", trackDomain)
            jsonObject.put("trackIds", jsonArr)
            jsonObject.put("activityAutoTracking", activityAutoTracking)
            jsonObject.put("autoTracking", autoTracking)
            jsonObject.put("batchSupport", batchSupport)
            jsonObject.put("exceptionLogLevel", exceptionLogLevel.name)
            jsonObject.put("fragmentsAutoTracking", fragmentsAutoTracking)
            jsonObject.put("logLevel", logLevel.name)
            jsonObject.put("requestPerBatch", requestPerBatch)
            jsonObject.put("requestsInterval", requestsInterval)
            jsonObject.put("shouldMigrate", shouldMigrate)
            jsonObject.put("versionInEachRequest", versionInEachRequest)
            jsonObject.put("everId",everId)
            jsonObject.put("everIdMode",everIdMode?.mode)
            return jsonObject.toString()
        } catch (e: Exception) {
            webtrekkLogger.error(e.message ?: "Unknown error")
        }
        return "{}"
    }

    override fun toString(): String {
        return "WebtrekkConfiguration(trackIds=$trackIds, trackDomain='$trackDomain', logLevel=$logLevel, requestsInterval=$requestsInterval, autoTracking=$autoTracking, fragmentsAutoTracking=$fragmentsAutoTracking, workManagerConstraints=$workManagerConstraints, okHttpClient=$okHttpClient, requestPerBatch=$requestPerBatch, batchSupport=$batchSupport, activityAutoTracking=$activityAutoTracking, exceptionLogLevel=$exceptionLogLevel, shouldMigrate=$shouldMigrate, versionInEachRequest=$versionInEachRequest, everId=$everId, userMatchingEnabled=$userMatchingEnabled, everIdMode=$everIdMode)"
    }

    companion object {
        internal fun fromJson(
            json: String,
        ): Config {
            val obj = JSONObject(json)

            val trackIdsJsonArray = obj.optJSONArray("trackIds") ?: JSONArray()

            val trackIds = mutableListOf<String>()

            for (i in 0 until trackIdsJsonArray.length()) {
                trackIds.add(trackIdsJsonArray.optString(i))
            }

            val mode = if (obj.has("everIdMode")) obj.optInt("everIdMode") else null
            val everIdMode = if (mode != null) GenerationMode.value(mode) else null

            return WebtrekkConfiguration(
                trackIds = trackIds,
                trackDomain = obj.optString("trackDomain"),
                logLevel = Logger.Level.valueOf(obj.optString("logLevel")),
                requestsInterval = obj.optLong("requestsInterval"),
                autoTracking = obj.optBoolean("autoTracking"),
                fragmentsAutoTracking = obj.optBoolean("fragmentsAutoTracking"),
                requestPerBatch = obj.optInt("requestPerBatch"),
                activityAutoTracking = obj.optBoolean("activityAutoTracking"),
                versionInEachRequest = obj.optBoolean("versionInEachRequest"),
                exceptionLogLevel = ExceptionType.valueOf(
                    obj.optString("exceptionLogLevel"),
                ),

                batchSupport = obj.optBoolean("batchSupport"),
                shouldMigrate = obj.optBoolean("shouldMigrate"),
                okHttpClient = DefaultConfiguration.OKHTTP_CLIENT,
                workManagerConstraints = DefaultConfiguration.WORK_MANAGER_CONSTRAINTS,
                userMatchingEnabled = if (obj.has("userMatchingEnabled")) obj.optBoolean("userMatchingEnabled") else false,
                everId = if (obj.has("everId")) obj.optString("everId") else null,
                everIdMode = everIdMode,
            )
        }
    }

}
