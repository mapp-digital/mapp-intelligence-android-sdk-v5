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

package webtrekk.android.sdk.domain

import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.utils.generateEverId

internal class Sessions(private val webtrekkSharedPrefs: WebtrekkSharedPrefs) {

    // if first time, generate the ever id alongside setting appFirstStart = 1 as it's app first start
    fun setEverId() {
        if (!webtrekkSharedPrefs.contains(WebtrekkSharedPrefs.EVER_ID_KEY)) {
            webtrekkSharedPrefs.everId = generateEverId()
                .also { webtrekkSharedPrefs.appFirstStart = "1" }
        }
    }

    fun getEverId(): String = webtrekkSharedPrefs.let {
        setEverId()

        return webtrekkSharedPrefs.everId
    }

    // after first getting app first start, set it to 0 forever
    fun getAppFirstStart(): String =
        webtrekkSharedPrefs.appFirstStart.also { webtrekkSharedPrefs.appFirstStart = "0" }

    fun startNewSession() {
        webtrekkSharedPrefs.fns = "1"
    }

    fun getCurrentSession(): String = webtrekkSharedPrefs.fns.also { webtrekkSharedPrefs.fns = "0" }

    fun optOut(value: Boolean) {
        webtrekkSharedPrefs.optOut = value
    }

    fun isOptOut(): Boolean {
        return webtrekkSharedPrefs.optOut
    }
}
