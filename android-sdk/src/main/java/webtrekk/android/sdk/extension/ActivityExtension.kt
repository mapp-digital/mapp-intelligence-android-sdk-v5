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
import webtrekk.android.sdk.TrackPageDetail
import webtrekk.android.sdk.TrackParams
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.util.appFirstOpen
import webtrekk.android.sdk.util.currentSession

/**
 * Extension function to create a [TrackRequest] from [Activity] to be inserted in the DB.
 */
internal fun Activity.toTrackRequest(needToTrack: Boolean = true): TrackRequest =
    TrackRequest(
        name = getName(),
        screenResolution = this.resolution(),
        forceNewSession = if (needToTrack) currentSession else "1",
        appFirstOpen = if (needToTrack) appFirstOpen else "1",
        appVersionName = this.appVersionName,
        appVersionCode = this.appVersionCode
    )

/**
 * Extension function to create a [TrackRequest] from [Fragment] to be inserted in the DB.
 */
internal fun Fragment.toTrackRequest(): TrackRequest =
    TrackRequest(
        name = getName(),
        screenResolution = this.context?.resolution(),
        forceNewSession = currentSession,
        appFirstOpen = appFirstOpen,
        appVersionName = this.context?.appVersionName,
        appVersionCode = this.context?.appVersionCode
    )

private fun <T : Any> T.getName(): String {
    var screenName = "${this.javaClass.`package`?.name}.${this.javaClass.simpleName}"
    if (this.javaClass.isAnnotationPresent(TrackPageDetail::class.java)) {
        val tracker = this.javaClass.getAnnotation(TrackPageDetail::class.java)
        if (tracker != null && tracker.contextName.isNotEmpty()) screenName = tracker.contextName
    }
    return screenName
}

internal fun <T : Any> T.getTrackerParams(): Array<TrackParams> {
    var trackers = emptyArray<TrackParams>()
    if (this.javaClass.isAnnotationPresent(TrackPageDetail::class.java)) {
        val tracker = this.javaClass.getAnnotation(TrackPageDetail::class.java)
        if (tracker != null && tracker.trackingParams.isNotEmpty()) trackers =
            tracker.trackingParams
    }
    return trackers
}
