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

package webtrekk.android.sdk.util

import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.core.WebtrekkImpl
import java.util.Locale
import kotlin.random.Random

/**
 * Utils for Webtrekk SDK.
 */
internal val currentSession: String
    inline get() = WebtrekkImpl.getInstance().sessions.getCurrentSession()

internal fun appFirstOpen(updateValue: Boolean = true): String =
    WebtrekkImpl.getInstance().sessions.getAppFirstOpen(updateValue = updateValue)

internal val appUpdated: Boolean
    inline get() = WebtrekkImpl.getInstance().isAppUpdate()

internal val trackDomain: String
    inline get() = WebtrekkImpl.getInstance().config.trackDomain

internal val batchSupported: Boolean
    inline get() = WebtrekkImpl.getInstance().config.batchSupport

internal val requestPerBatch: Int
    inline get() = WebtrekkImpl.getInstance().config.requestPerBatch

internal val trackIds: List<String>
    inline get() = WebtrekkImpl.getInstance().config.trackIds

internal val webtrekkLogger: Logger
    inline get() = WebtrekkImpl.getInstance().logger

internal var userId: String?
    inline get() = WebtrekkImpl.getInstance().sessions.getDmcUserId()
    set(value) = WebtrekkImpl.getInstance().sessions.setDmcUserId(value)

internal var appVersionInRequest: Boolean
    inline get() = WebtrekkImpl.getInstance().config.versionInEachRequest
    set(value) {
        WebtrekkImpl.getInstance().config.versionInEachRequest = value
    }

internal fun generateEverId(): String {
    val date = currentTimeStamp / 1000
    val random = Random

    val everId = "6${String.format(Locale.ENGLISH, "%010d%08d", date, random.nextLong(100000000))}"
    return everId
}
