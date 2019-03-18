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

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.room.RoomDatabase
import webtrekk.android.sdk.BuildConfig
import webtrekk.android.sdk.WebtrekkImpl
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

internal val currentOsVersion: String
    inline get() = Build.VERSION.RELEASE ?: ""

internal val currentApiLevel: Int
    inline get() = Build.VERSION.SDK_INT

internal val currentDeviceManufacturer: String
    inline get() = Build.MANUFACTURER ?: ""

internal val currentDeviceModel: String
    inline get() = Build.MODEL ?: ""

internal val currentCountry: String
    inline get() = Locale.getDefault().country

internal val currentLanguage: String
    inline get() = Locale.getDefault().language

internal val currentTimeZone: Int
    inline get() = TimeZone.getDefault().rawOffset / 1000 / 60 / 60

internal val currentTimeStamp: Long
    inline get() = System.currentTimeMillis()

internal val currentWebtrekkVersion: String
    inline get() = BuildConfig.VERSION_NAME

internal val currentEverId: String
    inline get() = WebtrekkImpl.getInstance().sessions.getEverId()

internal val currentSession: String
    @Synchronized inline get() = WebtrekkImpl.getInstance().sessions.getCurrentSession()

internal val appFirstStart: String
    @Synchronized inline get() = WebtrekkImpl.getInstance().sessions.getAppFirstStart()

internal val trackDomain: String
    inline get() = WebtrekkImpl.getInstance().config.trackDomain

internal val trackIds: List<String>
    inline get() = WebtrekkImpl.getInstance().config.trackIds

internal fun generateEverId(): String {
    val date = currentTimeStamp / 1000
    val random = Random

    return "6${String.format("%010d%08d", date, random.nextLong(100000000))}"
}

internal fun <T : RoomDatabase> buildRoomDatabase(
    context: Context,
    databaseName: String,
    database: Class<T>
): T = Room.databaseBuilder(
    context.applicationContext,
    database,
    databaseName
).build()
