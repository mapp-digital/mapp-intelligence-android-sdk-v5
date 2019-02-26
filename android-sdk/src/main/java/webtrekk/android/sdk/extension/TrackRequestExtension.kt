package webtrekk.android.sdk.extension

import android.app.Activity
import androidx.fragment.app.Fragment
import okhttp3.Request
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.*
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
        customParams.buildCustomParams()
}

internal fun DataTrack.buildUrlRequest(trackDomain: String, trackIds: List<String>): Request {
    return Request.Builder()
        .url(buildUrl(trackDomain, trackIds))
        .build()
}
