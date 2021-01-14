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

package webtrekk.android.sdk.core

import android.net.Uri
import webtrekk.android.sdk.InternalParam
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.extension.encodeToUTF8
import webtrekk.android.sdk.extension.generateUserAgent
import webtrekk.android.sdk.util.generateEverId

/**
 * The implementation of the [Sessions] based on [WebtrekkSharedPrefs].
 */
internal class SessionsImpl(private val webtrekkSharedPrefs: WebtrekkSharedPrefs) :
    Sessions {

    // if first time, generate the ever id alongside setting appFirstOpen = 1 as it's app first start.
    override fun setEverId() {
        if (!webtrekkSharedPrefs.contains(WebtrekkSharedPrefs.EVER_ID_KEY)) {
            webtrekkSharedPrefs.everId = generateEverId()
        }
    }

    override fun setEverId(everId: String) {
        webtrekkSharedPrefs.everId = everId
    }

    override fun getEverId(): String = webtrekkSharedPrefs.let {
        setEverId()
        return webtrekkSharedPrefs.everId
    }

    override fun getUserAgent(): String {
        return generateUserAgent
    }

    // after getting app first start, set it to 0 forever.
    override fun getAppFirstOpen(): String {
        val firstOpen = webtrekkSharedPrefs.appFirstOpen
        if (firstOpen == "1") {
            webtrekkSharedPrefs.appFirstOpen = "0"
        }
        return firstOpen
    }

    override fun startNewSession() {
        webtrekkSharedPrefs.fns = "1"
    }

    override fun getCurrentSession(): String {
        val newSession = webtrekkSharedPrefs.fns
        if (newSession == "1") {
            webtrekkSharedPrefs.fns = "0"
        }
        return newSession
    }

    override fun getAlias(): String {
        return webtrekkSharedPrefs.alias
    }

    override fun alias(alias: String) {
        webtrekkSharedPrefs.alias = alias
    }

    override fun userId(): String {
        return webtrekkSharedPrefs.userId
    }

    override fun userId(userId: String) {
        webtrekkSharedPrefs.userId = userId
    }

    override fun optOut(value: Boolean) {
        webtrekkSharedPrefs.optOut = value
    }

    override fun isOptOut(): Boolean {
        return webtrekkSharedPrefs.optOut
    }

    // Compares current app version number to the previously stored one, returns true if they not match, false otherwise.
    override fun isAppUpdated(currentAppVersion: String): Boolean {
        return if (!webtrekkSharedPrefs.contains(WebtrekkSharedPrefs.APP_VERSION)) {
            webtrekkSharedPrefs.appVersion = currentAppVersion

            false
        } else {
            val storedAppVersion = webtrekkSharedPrefs.appVersion

            storedAppVersion != currentAppVersion.apply {
                webtrekkSharedPrefs.appVersion = currentAppVersion
            }
        }
    }

    override fun migrate() {
        if (!webtrekkSharedPrefs.isMigrated) {
            if (webtrekkSharedPrefs.previousSharedPreferences.contains(WebtrekkSharedPrefs.EVER_ID_KEY)) {
                val everId = webtrekkSharedPrefs.previousEverId
                if (everId != "") {
                    webtrekkSharedPrefs.everId = everId
                    webtrekkSharedPrefs.appFirstOpen = "0"
                }
            }
            webtrekkSharedPrefs.isMigrated = true
        }
    }

    override fun getUrlKey(): Map<String, String> {
        val urlString = webtrekkSharedPrefs.saveUrlData
        val urlMap = mutableMapOf<String, String>()
        if (urlString.isNotBlank()) {
            val url = Uri.parse(urlString)
            val args: MutableSet<String> = url.queryParameterNames
            val type = url.getQueryParameter("webtrekk_type_param")
            args.forEach { key ->
                run {
                    val value = url.getQueryParameter(key)
                    if (!value.isNullOrBlank()) {
                        if (type != null) {
                            if (key == type)
                                urlMap[InternalParam.MEDIA_CODE_PARAM_EXCHANGER] =
                                    "$type=".encodeToUTF8() + value
                        }
                        if (key.contains("wt_cc")) {
                            urlMap[key.replace("wt_", "", true)] = value
                        }
                    }
                }
            }
            webtrekkSharedPrefs.saveUrlData = ""
        }
        return urlMap
    }

    override fun setUrl(urlString: Uri, mediaCode: String?) {
        val builtUri = urlString
            .buildUpon()
            .appendQueryParameter("webtrekk_type_param", mediaCode)
        webtrekkSharedPrefs.saveUrlData = builtUri.toString()
    }

    override fun updateUser(value: Boolean) {
        webtrekkSharedPrefs.isUserUpdated = value
    }

    override fun isUserUpdated(): Boolean {
        val updatedUser = webtrekkSharedPrefs.isUserUpdated
        if (updatedUser) {
            webtrekkSharedPrefs.isUserUpdated = false
        }
        return updatedUser
    }
}
