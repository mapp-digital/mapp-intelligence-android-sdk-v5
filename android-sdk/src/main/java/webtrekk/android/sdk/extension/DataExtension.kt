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

import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import webtrekk.android.sdk.InternalParam
import webtrekk.android.sdk.Param
import webtrekk.android.sdk.TrackParams
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.util.currentWebtrekkVersion
import webtrekk.android.sdk.util.currentOsVersion
import webtrekk.android.sdk.util.currentDeviceManufacturer
import webtrekk.android.sdk.util.currentDeviceModel
import webtrekk.android.sdk.util.currentLanguage
import webtrekk.android.sdk.util.currentCountry
import webtrekk.android.sdk.util.userId

/**
 * This file contains extension & helper functions used to form the request url from [TrackRequest] and [DataTrack].
 */
internal val TrackRequest.webtrekkRequestParams
    // The webtrekk version number must be sent without '.'.
    inline get() = "${
        webtrekkVersion.replace(
            ".",
            ""
        )
    },${name.encodeToUTF8()},0,$screenResolution,0,0,$timeStamp,0,0,0"

internal val TrackRequest.userAgent
    inline get() = "Tracking Library $webtrekkVersion (Android $osVersion; $deviceManufacturer $deviceModel; ${language}_$country)"

internal val generateUserAgent
    inline get() = "Tracking Library $currentWebtrekkVersion (Android $currentOsVersion; $currentDeviceManufacturer $currentDeviceModel; ${currentLanguage}_$currentCountry)"

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
    this.forEach {
        val paramVal =
            // For media code param, it needs to be double encoded
            if (it.paramKey == Param.MEDIA_CODE) "wt_mc=".encodeToUTF8() + it.paramValue else it.paramValue
        val paramKey =
            if (it.paramKey == InternalParam.MEDIA_CODE_PARAM_EXCHANGER) Param.MEDIA_CODE else it.paramKey

        string.append("&${paramKey.encodeToUTF8()}=${paramVal.encodeToUTF8()}")
    }

    return string.toString()
}

internal fun DataTrack.buildUrl(
    trackDomain: String,
    trackIds: List<String>,
    currentEverId: String
): String {
    return buildUrlOnly(trackDomain, trackIds) +
        this.buildBody(currentEverId)
}

internal fun buildUrlOnly(
    trackDomain: String,
    trackIds: List<String>
): String {
    return "$trackDomain/${trackIds.joinToString(separator = ",")}"
}

internal fun List<DataTrack>.buildBatchUrl(
    trackDomain: String,
    trackIds: List<String>,
    currentEverId: String
): String {
    return buildUrlOnly(trackDomain, trackIds) + "/batch?" + "${UrlParams.EVER_ID}=$currentEverId" +
        "&${UrlParams.USER_AGENT}=${this[0].trackRequest.userAgent.encodeToUTF8()}"
}

internal fun DataTrack.buildBody(currentEverId: String, withOutBatching: Boolean = true): String {
    var stringBuffer: String = if (withOutBatching)
        "/wt?"
    else {
        "wt?"
    }
    stringBuffer += "${UrlParams.WEBTREKK_PARAM}=${this.trackRequest.webtrekkRequestParams}" +
        "&${UrlParams.LANGUAGE}=${this.trackRequest.language}"

    stringBuffer += if (this.trackRequest.forceNewSession == "1") {
        "&${UrlParams.APP_ONE}=${this.trackRequest.appFirstOpen}" +
            "&${UrlParams.FORCE_NEW_SESSION}=${this.trackRequest.forceNewSession}" +
            "&${UrlParams.APP_FIRST_OPEN}=${this.trackRequest.appFirstOpen}" +
            "&${UrlParams.ANDROID_API_LEVEL}=${this.trackRequest.apiLevel}" +
            "&${UrlParams.APP_VERSION_NAME}=${this.trackRequest.appVersionName}" +
            "&${UrlParams.APP_VERSION_CODE}=${this.trackRequest.appVersionCode}"
    } else {
        ""
    }

    if (withOutBatching) {
        stringBuffer += "&${UrlParams.EVER_ID}=$currentEverId" +
            "&${UrlParams.USER_AGENT}=${this.trackRequest.userAgent.encodeToUTF8()}"
    }
    if (this.trackRequest.forceNewSession == "1" && userId != "") {
        stringBuffer += "&${UrlParams.USER_ID}=$userId"
    }
    stringBuffer += customParams.buildCustomParams()

    return stringBuffer
}

internal fun DataTrack.buildUrlRequest(
    trackDomain: String,
    trackIds: List<String>,
    currentEverId: String
): Request {

    return Request.Builder()
        .url(buildUrl(trackDomain, trackIds, currentEverId))
        .build()
}

internal fun List<DataTrack>.buildPostRequest(
    trackDomain: String,
    trackIds: List<String>,
    currentEverId: String
): Request {
    return Request.Builder()
        .url(buildBatchUrl(trackDomain, trackIds, currentEverId))
        .post(
            this.buildUrlRequests(currentEverId)
                .toRequestBody("text/plain".toMediaTypeOrNull())
        )
        .build()
}

internal fun List<DataTrack>.buildUrlRequests(currentEverId: String): String {
    var string = ""
    this.forEach { dataTrack ->
        string += dataTrack.buildBody(currentEverId, false) + "\n"
    }
    return string
}

internal fun Array<TrackParams>.toParam(): Map<String, String> =
    map { it.paramKey to it.paramVal }.toMap()