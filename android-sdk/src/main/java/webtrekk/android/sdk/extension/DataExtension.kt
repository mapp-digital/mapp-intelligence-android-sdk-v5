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

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import webtrekk.android.sdk.ActiveConfig
import webtrekk.android.sdk.CampaignParam
import webtrekk.android.sdk.DefaultConfiguration.exactSdkVersion
import webtrekk.android.sdk.DefaultConfiguration.platform
import webtrekk.android.sdk.InternalParam
import webtrekk.android.sdk.TrackParams
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.util.appVersionInRequest
import webtrekk.android.sdk.util.currentCountry
import webtrekk.android.sdk.util.currentDeviceManufacturer
import webtrekk.android.sdk.util.currentDeviceModel
import webtrekk.android.sdk.util.currentLanguage
import webtrekk.android.sdk.util.currentOsVersion
import webtrekk.android.sdk.util.currentWebtrekkVersion

/**
 * This file contains extension & helper functions used to form the request url from [TrackRequest] and [DataTrack].
 */
internal val TrackRequest.webtrekkRequestParams
    // The webtrekk version number must be sent without '.'.
    inline get() = "${
        webtrekkVersion.split(".")
            .subList(0, 3)
            .joinToString(separator = "")
            .substring(0, 3)

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

internal fun List<CustomParam>.buildCustomParams(
    anonymous: Boolean = false,
    anonymousParam: Set<String> = emptySet()
): String {
    if (this == emptyArray<CustomParam>()) return ""
    val string = StringBuilder()
    for (it in this) {
        // For media code param, it needs to be double encoded
        var paramKey: String = it.paramKey
        var paramVal = it.paramValue

        if (paramKey == InternalParam.MEDIA_CODE_PARAM_EXCHANGER)
            paramKey = CampaignParam.MEDIA_CODE
        if (paramKey == CampaignParam.MEDIA_CODE) {
            if (!(paramVal.contains("=") || paramVal.contains("%3D") || paramVal.contains("%253D"))) {
                paramVal = (InternalParam.WT_MC_DEFAULT + "=").encodeToUTF8() + paramVal
            }
        }
        if (!anonymous || !anonymousParam.contains(paramKey)) {
            string.append("&${paramKey.encodeToUTF8()}=${paramVal.encodeToUTF8()}")
        }
    }
    return string.toString()
}

internal fun DataTrack.buildUrl(
    activeConfig: ActiveConfig
): String {
    return buildUrlOnly(activeConfig.trackDomains, activeConfig.trackIds) +
            this.buildBody(
                withOutBatching = true,
                activeConfig = activeConfig
            )
}

internal fun buildUrlOnly(
    trackDomain: String,
    trackIds: List<String>
): String {
    return "$trackDomain/${trackIds.joinToString(separator = ",")}"
}

internal fun List<DataTrack>.buildBatchUrl(
    activeConfig: ActiveConfig
): String {
    return buildUrlOnly(
        activeConfig.trackDomains,
        activeConfig.trackIds
    ) + "/batch?" + anonymousEid(
        activeConfig.isAnonymous,
        activeConfig.everId
    ) +
            addParam(
                UrlParams.USER_AGENT,
                this[0].trackRequest.userAgent.encodeToUTF8(),
                activeConfig.anonymousParams,
                activeConfig.isAnonymous
            )
}

internal fun DataTrack.buildBody(
    withOutBatching: Boolean = true,
    activeConfig: ActiveConfig
): String {
    var stringBuffer: String = if (withOutBatching)
        "/wt?"
    else {
        "wt?"
    }
    stringBuffer += "${UrlParams.WEBTREKK_PARAM}=${this.trackRequest.webtrekkRequestParams}" +
            addParam(
                UrlParams.LANGUAGE,
                this.trackRequest.language,
                activeConfig.anonymousParams,
                activeConfig.isAnonymous
            )

    stringBuffer += if (this.trackRequest.forceNewSession == "1") {
        "&${UrlParams.APP_ONE}=${this.trackRequest.appFirstOpen}" +
                "&${UrlParams.FORCE_NEW_SESSION}=${this.trackRequest.forceNewSession}" +
                addParam(
                    UrlParams.APP_FIRST_OPEN,
                    this.trackRequest.appFirstOpen,
                    activeConfig.anonymousParams,
                    activeConfig.isAnonymous
                ) +
                addParam(
                    UrlParams.ANDROID_API_LEVEL,
                    this.trackRequest.apiLevel,
                    activeConfig.anonymousParams,
                    activeConfig.isAnonymous
                ) +
                addParam(
                    UrlParams.APP_VERSION_NAME,
                    this.trackRequest.appVersionName,
                    activeConfig.anonymousParams,
                    activeConfig.isAnonymous
                ) +
                addParam(
                    UrlParams.APP_VERSION_CODE,
                    this.trackRequest.appVersionCode,
                    activeConfig.anonymousParams,
                    activeConfig.isAnonymous
                )
    } else {
        var value = ""
        if (appVersionInRequest) {
            value =
                addParam(
                    UrlParams.APP_VERSION_NAME,
                    this.trackRequest.appVersionName,
                    activeConfig.anonymousParams,
                    activeConfig.isAnonymous
                ) + addParam(
                    UrlParams.APP_VERSION_CODE,
                    this.trackRequest.appVersionCode,
                    activeConfig.anonymousParams,
                    activeConfig.isAnonymous
                )
        }
        value
    }

    if (withOutBatching) {
        stringBuffer += "&${anonymousEid(activeConfig.isAnonymous, activeConfig.everId)}" +
                addParam(
                    UrlParams.USER_AGENT,
                    this.trackRequest.userAgent.encodeToUTF8(),
                    activeConfig.anonymousParams,
                    activeConfig.isAnonymous
                )
    }

    stringBuffer += addParam(
        UrlParams.EXACT_SDK_VERSION,
        exactSdkVersion,
        activeConfig.anonymousParams,
        activeConfig.isAnonymous
    ) + addParam(
        UrlParams.PLATFORM,
        platform,
        activeConfig.anonymousParams,
        activeConfig.isAnonymous
    )

    stringBuffer += "&${UrlParams.CONFIGURED_SDK_FEATURES}=${activeConfig.calculateUsageParam()}"

    if (activeConfig.temporarySessionId?.isNotBlank() == true) {
        stringBuffer += "&${UrlParams.TEMPORARY_USER_ID_NAME}=${activeConfig.temporarySessionId}"
    }

    // filter custom parameters and look for uc701 (EmailReceiverId)
    val emailReceiverID = customParams.find {
        (it.paramKey == UrlParams.USER_ID)
    }

    // set global EmailReceiverId only if custom EmailReceiverId not set
    if (emailReceiverID == null && !activeConfig.isAnonymous && activeConfig.isUserMatching && !activeConfig.userMatchingId.isNullOrEmpty()) {
        stringBuffer += "&${UrlParams.USER_ID}=${activeConfig.userMatchingId}"
    }

    val customParamsString = customParams.buildCustomParams(
        activeConfig.isAnonymous,
        activeConfig.anonymousParams
    )
    stringBuffer += customParamsString

    return stringBuffer
}

internal fun DataTrack.buildUrlRequest(
    activeConfig: ActiveConfig
): Request {
    return Request.Builder()
        .url(buildUrl(activeConfig))
        .build()
}

internal fun List<DataTrack>.buildPostRequest(
    activeConfig: ActiveConfig
): Request {
    return Request.Builder()
        .url(buildBatchUrl(activeConfig))
        .post(
            this.buildUrlRequests(activeConfig)
                .toRequestBody("text/plain".toMediaTypeOrNull())
        )
        .build()
}

internal fun List<DataTrack>.buildUrlRequests(
    activeConfig: ActiveConfig
): String {
    var string = ""
    this.forEach { dataTrack ->
        string += dataTrack.buildBody(
            withOutBatching = false,
            activeConfig = activeConfig
        ) + "\n"
    }
    return string
}

internal fun Array<TrackParams>.toParam(): Map<String, String> =
    map { it.paramKey to it.paramVal }.toMap()

internal fun addParam(
    param: String,
    value: String?,
    anonymousParam: Set<String>,
    anonymous: Boolean,
    separator: String = "&"
): String {
    return if (anonymousParam.contains(param) && anonymous) {
        ""
    } else {
        "$separator$param=$value"
    }
}

private fun anonymousEid(anonymous: Boolean, currentEverId: String?): String {
    return if (anonymous || currentEverId.isNullOrEmpty()) {
        "${UrlParams.EVER_ID_ANONYMOUS}=1"
    } else {
        "${UrlParams.EVER_ID}=$currentEverId"
    }
}
