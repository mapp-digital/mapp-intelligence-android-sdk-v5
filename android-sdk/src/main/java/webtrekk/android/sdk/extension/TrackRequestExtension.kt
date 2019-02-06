package webtrekk.android.sdk.extension

import android.app.Activity
import androidx.fragment.app.Fragment
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.*
import webtrekk.android.sdk.util.currentEverId

internal fun Activity.toTrackRequest(): TrackRequest =
    TrackRequest(name = this.localClassName, screenResolution = this.resolution())

internal fun Fragment.toTrackRequest(): TrackRequest =
    TrackRequest(name = this.toString(), screenResolution = this.context?.resolution())

internal val TrackRequest.webtrekkParams
    inline get() = "$webtrekkVersion,${name.encodeToUTF8()},0,$screenResolution,0,0,$timeStamp,0,0,0"

internal val TrackRequest.userAgent
    inline get() = "Tracking Library $webtrekkVersion (Android $osVersion; $deviceManufacturer $deviceModel; ${language}_$country)"

internal fun List<CustomParam>.buildCustomParams(): String {
    if (this == emptyArray<CustomParam>()) return ""

    val string = StringBuilder()
    this.forEach { string.append("&${it.paramKey.encodeToUTF8()}=${it.paramValue.encodeToUTF8()}") }
    return string.toString()
}

internal fun DataTrack.buildUrl(trackDomain: String, trackIds: List<String>): String {
    return "$trackDomain/${trackIds.joinToString(separator = ",")}" +
        "/wt" +
        "?${UrlParams.WEBTREKK_PARAM.value}=${this.trackRequest.webtrekkParams}" +
        "&${UrlParams.USER_AGENT.value}=${this.trackRequest.userAgent.encodeToUTF8()}" +
        "&${UrlParams.EVER_ID.value}=$currentEverId" +
        "&${UrlParams.APP_FIRST_START.value}=${this.trackRequest.one}" +
        "&${UrlParams.FORCE_NEW_SESSION.value}=${this.trackRequest.fns}" +
        customParams.buildCustomParams()
}
