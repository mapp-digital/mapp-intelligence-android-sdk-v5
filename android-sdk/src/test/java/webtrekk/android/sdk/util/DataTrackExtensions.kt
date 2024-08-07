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

import okhttp3.Request
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.extension.buildCustomParams
import webtrekk.android.sdk.extension.encodeToUTF8
import webtrekk.android.sdk.extension.userAgent
import webtrekk.android.sdk.extension.webtrekkRequestParams

internal fun DataTrack.buildUrlForTesting(trackDomain: String, trackIds: List<String>): String {
    return "$trackDomain/${trackIds.joinToString(separator = ",")}" +
        "/wt" +
        "?${UrlParams.WEBTREKK_PARAM}=${this.trackRequest.webtrekkRequestParams}" +
        "&${UrlParams.USER_AGENT}=${this.trackRequest.userAgent.encodeToUTF8()}" +
        "&${UrlParams.EVER_ID}=123456789" +
        "&${UrlParams.APP_FIRST_OPEN}=${this.trackRequest.appFirstOpen}" +
        "&${UrlParams.FORCE_NEW_SESSION}=${this.trackRequest.forceNewSession}" +
        customParams.buildCustomParams(anonymous = true, anonymousParam = setOf("uc709", "uc703"))
}

internal fun DataTrack.buildUrlRequestForTesting(
    trackDomain: String,
    trackIds: List<String>
): Request {
    return Request.Builder()
        .url(buildUrlForTesting(trackDomain, trackIds))
        .build()
}
