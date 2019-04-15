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

package webtrekk.android.sdk.extension

import android.app.Activity
import androidx.fragment.app.Fragment
import okhttp3.Request
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.util.currentEverId

internal fun Activity.toTrackRequest(): TrackRequest =
    TrackRequest(name = this.localClassName, screenResolution = this.resolution())

internal fun Fragment.toTrackRequest(): TrackRequest =
    TrackRequest(name = this.javaClass.simpleName, screenResolution = this.context?.resolution())

internal val TrackRequest.webtrekkRequestParams
    inline get() = "$webtrekkVersion,${name.encodeToUTF8()},0,$screenResolution,0,0,$timeStamp,0,0,0"

internal val TrackRequest.userAgent
    inline get() = "Tracking Library $webtrekkVersion (Android $osVersion; $deviceManufacturer $deviceModel; ${language}_$country)"

internal fun Map<String, String>.toCustomParams(trackRequestId: Long): List<CustomParam> {
    return this.map {
        CustomParam(
            trackId = trackRequestId,
            paramKey = it.key,
            paramValue = it.value
        )
    }
}

internal fun List<CustomParam>.buildCustomParams(): String {
    if (this == emptyArray<CustomParam>()) return ""

    val string = StringBuilder()
    this.forEach { string.append("&${it.paramKey.encodeToUTF8()}=${it.paramValue.encodeToUTF8()}") }

    return string.toString()
}

internal fun DataTrack.buildUrl(trackDomain: String, trackIds: List<String>): String {
    return "$trackDomain/${trackIds.joinToString(separator = ",")}" +
        "/wt" +
        "?${UrlParams.WEBTREKK_PARAM}=${this.trackRequest.webtrekkRequestParams}" +
        "&${UrlParams.USER_AGENT}=${this.trackRequest.userAgent.encodeToUTF8()}" +
        "&${UrlParams.EVER_ID}=$currentEverId" +
        "&${UrlParams.APP_FIRST_START}=${this.trackRequest.one}" +
        "&${UrlParams.FORCE_NEW_SESSION}=${this.trackRequest.fns}" +
        "&${UrlParams.LANGUAGE}=${this.trackRequest.language}" +
        customParams.buildCustomParams()
}

internal fun DataTrack.buildUrlRequest(trackDomain: String, trackIds: List<String>): Request {
    return Request.Builder()
        .url(buildUrl(trackDomain, trackIds))
        .build()
}
