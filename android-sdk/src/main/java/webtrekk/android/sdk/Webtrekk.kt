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

/**
 * Webtrekk is a library used to track your app usages and custom events.
 *
 * {@code
 * A {@link WebtrekkConfiguration}
 *
 * val webtrekkConfigurations = WebtrekkConfiguration.Builder(listOf("1"), "https://www.webtrekk.com")
 * .LOG_LEVEL_VALUE(Logger.Level.BASIC)
 * .SEND_DELAY_VALUE(TimeUnit.MINUTES, 15)
 * .disableAutoTracking()
 * .build()
 *
 * val webtrekk = Webtrekk.getInstance()
 * webtrekk.init(this, webtrekkConfigurations)
 *
 */
abstract class Webtrekk protected constructor() {

    /**
     * Must be called to initialize Webtrekk with the application context and webtrekk configurations.
     *
     * Invoking any other method before initializing, will cause the SDK to throw an IllegalStateException.
     */
    abstract fun init(context: Context, config: Config)

    abstract fun trackPage(context: Context, customPageName: String? = null, trackingParams: Map<String, String> = emptyMap())

    abstract fun trackCustomPage(pageName: String, trackingParams: Map<String, String>)

    /**
     * we may use event name or latest activity name or latest page name
     * only difference between page and event is event has CT
     * last page name or last activity - should include page name on tracker
     * ON HOLD
     */
    abstract fun trackCustomEvent(eventName: String, trackingParams: Map<String, String>)

    abstract fun optOut(value: Boolean)

    /**
     * Returns [Boolean] true if the user is opt'd-out, false otherwise.
     */
    abstract fun hasOptOut(): Boolean

    /**
     * Returns [String] the user ever id which is associated per each unique user.
     */
    abstract fun getEverId(): String

    companion object {

        @JvmStatic
        fun getInstance(): Webtrekk {
            return WebtrekkImpl.getInstance()
        }
    }
}
