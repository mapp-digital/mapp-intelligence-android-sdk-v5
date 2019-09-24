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
import androidx.work.NetworkType
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * A singleton object that holds all the default configurations in the library.
 *
 * The default configurations are set in [WebtrekkConfiguration] in case that you not override the configurations there,
 * You must use instance of [WebtrekkConfiguration] to set up all the necessary configurations that will be used in the library.
 *
 * @see [WebtrekkConfiguration] to customize those configurations.
 */
object DefaultConfiguration {

    /**
     * The auto track in the configurations is enabled by default.
     *
     * To disable the auto track, call [WebtrekkConfiguration.Builder.disableAutoTracking]
     */
    const val AUTO_TRACK_ENABLED = true

    /**
     * The batch support in the configurations is disabled by default.
     *
     * To enable the batch support, call [WebtrekkConfiguration.Builder.setBatchSupport]  with true
     */

    const val BATCH_SUPPORT_ENABLED = true
    /**
     * The request per batch in the configurations is 10000 by default.
     *
     * To can be changed in call [WebtrekkConfiguration.Builder.setBatchSupport]
     */
    const val REQUEST_PER_BATCH = 10000

    /**
     * The auto track of fragments in the configurations is enabled by default. And it's only enabled when [Config.autoTracking]
     * is enabled.
     *
     * To disable the auto tracking of fragments, call [WebtrekkConfiguration.Builder.disableFragmentsAutoTracking]
     */
    const val FRAGMENTS_AUTO_TRACK_ENABLED = true

    /**
     * The auto track of fragments in the configurations is enabled by default. And it's only enabled when [Config.autoTracking]
     * is enabled.
     *
     * To disable the auto tracking of fragments, call [WebtrekkConfiguration.Builder.disableFragmentsAutoTracking]
     */
    const val ACTIVITY_AUTO_TRACK_ENABLED = true

    /**
     * The default interval time that's used in [WebtrekkConfiguration.Builder.requestsInterval].
     */
    const val REQUESTS_INTERVAL: Long = 15 // minutes

    /**
     * The default time unit that is used in [WebtrekkConfiguration.Builder.requestsInterval].
     */
    val TIME_UNIT_VALUE = TimeUnit.MINUTES

    /**
     * The default log level that is used in [WebtrekkConfiguration.Builder.logLevel].
     */
    val LOG_LEVEL_VALUE = Logger.Level.BASIC

    /**
     * The default work manager constraints that are used by the library when sending the requests to the server.
     *
     * @see [WebtrekkConfiguration.Builder.workManagerConstraints]
     */
    val WORK_MANAGER_CONSTRAINTS = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()

    /**
     * The default okHttpClient configurations that will be used by the library for networking.
     *
     * @see [WebtrekkConfiguration.Builder.okHttpClient]
     */
    val OKHTTP_CLIENT: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()
}
