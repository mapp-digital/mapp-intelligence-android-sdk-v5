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

package webtrekk.android.sdk.data

import android.content.Context
import android.content.SharedPreferences

/**
 * A class that manages all of Webtrekk internal SharedPreferences.
 */
internal class WebtrekkSharedPrefs(context: Context) {

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    var everId: String
        inline get() = sharedPreferences.getString(EVER_ID_KEY, "") ?: ""
        set(value) = sharedPreferences.edit().putString(EVER_ID_KEY, value).apply()

    var appFirstOpen: String
        inline get() = sharedPreferences.getString(APP_FIRST_OPEN, "1") ?: "1"
        set(value) = sharedPreferences.edit().putString(APP_FIRST_OPEN, value).apply()

    var fns: String
        inline get() = sharedPreferences.getString(NEW_SESSION_KEY, "1") ?: "1"
        set(value) = sharedPreferences.edit().putString(NEW_SESSION_KEY, value).apply()

    var optOut: Boolean
        inline get() = sharedPreferences.getBoolean(USER_OPT_OUT, false)
        set(value) = sharedPreferences.edit().putBoolean(USER_OPT_OUT, value).apply()

    var appVersion: String
        inline get() = sharedPreferences.getString(APP_VERSION, "") ?: ""
        set(value) = sharedPreferences.edit().putString(APP_VERSION, value).apply()

    fun contains(key: String): Boolean = sharedPreferences.contains(key)

    companion object {

        const val SHARED_PREFS_NAME = "webtrekk_sharedPref"
        const val EVER_ID_KEY = "everId"
        const val APP_FIRST_OPEN = "appFirstOpen"
        const val NEW_SESSION_KEY = "forceNewSession"
        const val USER_OPT_OUT = "optOut"
        const val APP_VERSION = "appVersion"
    }
}
