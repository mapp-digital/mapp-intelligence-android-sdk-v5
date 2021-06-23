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

/**
 * A configuration interface for customizing [Webtrekk] configurations.
 *
 * You should NOT implement this interface. Use [WebtrekkConfiguration] to set up the configuration, which is a concrete implementation of
 * this interface with a builder pattern.
 *
 * Ideally, you must set valid [trackIds] and [trackDomain], without setting them up, webtrekk won't send
 * any tracking data.
 *
 * All other config parameters have default values, which you may override their values.
 * @see [DefaultConfiguration] for the default values (behaviour) of each parameter.
 */
interface Config {

    /**
     * The [trackIds] that you get in your Webtrekk's account. [trackIds] must be set in the configuration,
     * otherwise webtrekk won't send any tracking data.
     *
     * Could be just one track Id or list of track Ids, but there must be at least one track Id.
     */
    var trackIds: List<String>

    /**
     * The [trackDomain] domain that all the analytics data will be sent to. Make sure it's a valid domain.
     *
     * [trackDomain] must be set in the configuration, otherwise webtrekk won't send any tracking data.
     */
    var trackDomain: String

    /**
     * [logLevel], the log level that is used through the library.
     *
     * In [WebtrekkConfiguration] the default is [Logger.Level.BASIC].
     * To disable logging, then set up the [logLevel] to [Logger.Level.NONE]
     */
    val logLevel: Logger.Level

    /**
     * [requestsInterval] the interval period in [java.util.concurrent.TimeUnit.MILLISECONDS]
     * when [Webtrekk] sends the cached tracking data through network to the analytics.
     *
     * Webtrekk uses [androidx.work.WorkManager] for enqueuing and sending the requests in the background, for battery optimization.
     *
     * *NOTE* the minimum interval period is 15 minutes.
     * In [WebtrekkConfiguration] the default is [DefaultConfiguration.REQUESTS_INTERVAL]
     */
    val requestsInterval: Long

    /**
     * [autoTracking] set to true to enable the auto tracking. Set to false to disable the auto tracking.
     *
     * By default, [autoTracking] will auto track every Activity and Fragment, to just auto track of activities and disable fragments,
     * set [fragmentsAutoTracking] to false.
     *
     * In [WebtrekkConfiguration] the default is [DefaultConfiguration.AUTO_TRACK_ENABLED] enabled.
     * To disable the auto tracking in [WebtrekkConfiguration], call [WebtrekkConfiguration.Builder.disableAutoTracking]
     */
    val autoTracking: Boolean

    /**
     * [fragmentsAutoTracking] set to true to enable auto tracking of fragments when auto tracking is enabled.
     * Set to false to disable auto tracking of fragments.
     *
     * *NOTE* [autoTracking] must be enabled first, to start auto tracking of fragments.
     *
     * In [WebtrekkConfiguration] the default is [DefaultConfiguration.FRAGMENTS_AUTO_TRACK_ENABLED] enabled.
     * To disable the auto tracking of fragments in [WebtrekkConfiguration], call [WebtrekkConfiguration.Builder.disableFragmentsAutoTracking]
     */
    val fragmentsAutoTracking: Boolean

    /**
     * [fragmentsAutoTracking] set to true to enable auto tracking of fragments when auto tracking is enabled.
     * Set to false to disable auto tracking of fragments.
     *
     * *NOTE* [autoTracking] must be enabled first, to start auto tracking of fragments.
     *
     * In [WebtrekkConfiguration] the default is [DefaultConfiguration.FRAGMENTS_AUTO_TRACK_ENABLED] enabled.
     * To disable the auto tracking of fragments in [WebtrekkConfiguration], call [WebtrekkConfiguration.Builder.disableFragmentsAutoTracking]
     */
    val activityAutoTracking: Boolean

    /**
     * Choose [exceptionLogLevel] type to track different exceptions individually ([ExceptionType.UNCAUGHT], [ExceptionType.CAUGHT], [ExceptionType.CUSTOM]),
     * or different combinations of them. ([ExceptionType.ALL], [ExceptionType.UNCAUGHT_AND_CUSTOM], [ExceptionType.CUSTOM_AND_CAUGHT])
     */
    val exceptionLogLevel: ExceptionType

    /**
     * [workManagerConstraints] the constraints that will be used by the work manager to send the requests.
     *
     * Those constraints are important for your app battery performance and optimization. You can configure constraints
     * like if the device is charging or when the battery is not low, so the users won't notice any overhead performance in your app.
     *
     * In [WebtrekkConfiguration] the default constraints [DefaultConfiguration.WORK_MANAGER_CONSTRAINTS].
     *
     * *NOTE* make sure that you enable the network connection constraint, so that the requests will be sent only when
     * the network connection is on.
     */
    val workManagerConstraints: Constraints

    /**
     * [okHttpClient] is the client that is used for networking.
     *
     * You can customize it, like adding your pinning certificates, configuring SSL socket or adding interceptor, etc.
     *
     * In [WebtrekkConfiguration] the defailt okHttplclient is [DefaultConfiguration.OKHTTP_CLIENT]
     */
    val okHttpClient: OkHttpClient

    /**
     * [batchSupport] is the client that is used for allowing request batching.
     *
     * You can customize it, if allow sdk will send data in the batch
     *
     * In [WebtrekkConfiguration] the defailt requestPerBatch is [DefaultConfiguration.BATCH_SUPPORT_ENABLED]
     */
    val batchSupport: Boolean

    /**
     * [shouldMigrate] set to true to enable migration of ever id and one parameter values from sdk version 4.
     */
    val shouldMigrate: Boolean

    /**
     * [requestPerBatch] is the client that is used for networking.
     *
     * You can customize it, biggest number will allow biggest batch
     *
     * In [WebtrekkConfiguration] the defailt requestPerBatch is [DefaultConfiguration.REQUEST_PER_BATCH]
     */
    val requestPerBatch: Int

    /**
     * [versionInEachRequest] if versionInEachRequest is true we will send apk version in each request
     */
    val versionInEachRequest: Boolean
}