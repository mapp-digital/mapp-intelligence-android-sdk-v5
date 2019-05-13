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

package webtrekk.android.sdk.core.util

import android.os.Build
import webtrekk.android.sdk.core.BuildConfig
import java.util.Locale
import java.util.TimeZone
import java.util.Date
import java.util.concurrent.TimeUnit

val currentOsVersion: String
    inline get() = Build.VERSION.RELEASE ?: ""

val currentApiLevel: Int
    inline get() = Build.VERSION.SDK_INT

val currentDeviceManufacturer: String
    inline get() = Build.MANUFACTURER ?: ""

val currentDeviceModel: String
    inline get() = Build.MODEL ?: ""

val currentCountry: String
    inline get() = Locale.getDefault().country

val currentLanguage: String
    inline get() = Locale.getDefault().language

val currentTimeZone: Int
    inline get() {
        val timeZone = TimeZone.getDefault()
        var offset = timeZone.rawOffset
        if (timeZone.inDaylightTime(Date())) {
            offset += timeZone.dstSavings
        }

        return TimeUnit.HOURS.convert(offset.toLong(), TimeUnit.MILLISECONDS).toInt()
    }

val currentTimeStamp: Long
    inline get() = System.currentTimeMillis()

val currentWebtrekkVersion: String
    inline get() = BuildConfig.VERSION_NAME