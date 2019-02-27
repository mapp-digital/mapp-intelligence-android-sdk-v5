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

class WebtrekkConfiguration private constructor(
    override val trackIds: List<String>,
    override val trackDomain: String,
    override val logLevel: Logger.Level,
    override val sendDelay: Long,
    override val autoTracking: Boolean,
    override val workManagerConstraints: Constraints,
    override val okHttpClient: OkHttpClient
) : Config {

    class Builder(private val trackIds: List<String>, private val trackDomain: String) {
        private var logLevel = DefaultConfiguration.LOG_LEVEL_VALUE
        private var sendDelay =
            DefaultConfiguration.TIME_UNIT_VALUE.toMillis(DefaultConfiguration.SEND_DELAY_VALUE)
        private var autoTracking = DefaultConfiguration.AUTO_TRACK_ENABLED
        private var constraints = DefaultConfiguration.WORK_MANAGER_CONSTRAINTS
        private var okHttpClientBuilder = DefaultConfiguration.OKHTTP_CLIENT

        @JvmName("setLogLevel")
        fun logLevel(logLevel: Logger.Level) = apply { this.logLevel = logLevel }

        @JvmName("setSendDelay")
        fun sendDelay(timeUnit: TimeUnit = TimeUnit.MINUTES, sendDelay: Long) =
            apply { this.sendDelay = timeUnit.toMillis(sendDelay) }

        fun disableAutoTracking() = apply { this.autoTracking = false }

        @JvmName("setWorkManagerConstraints")
        fun workManagerConstraints(constraints: Constraints) =
            apply { this.constraints = constraints }

        @JvmName("setOkHttpClient")
        fun okHttpClient(okHttpClient: OkHttpClient) =
            apply { this.okHttpClientBuilder = okHttpClient }

        fun build() = WebtrekkConfiguration(
            trackIds.validateEntireList("trackId"),
            trackDomain.nullOrEmptyThrowError("trackDomain"),
            logLevel,
            sendDelay,
            autoTracking,
            constraints,
            okHttpClientBuilder
        )
    }
}
