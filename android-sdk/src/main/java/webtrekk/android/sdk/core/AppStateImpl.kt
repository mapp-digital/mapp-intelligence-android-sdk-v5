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

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import webtrekk.android.sdk.StopTrack
import webtrekk.android.sdk.data.entity.DataAnnotationClass
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.extension.getTrackerParams
import webtrekk.android.sdk.extension.toTrackRequest

/**
 * The implementation of [AppState]. This class listens to both activity and fragments life cycles, and automatically creates a [TrackRequest] from the data coming from the life cycles.
 */
internal class AppStateImpl : AppState<DataAnnotationClass>() {

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)

        activity.let {
            val needToTrack = !activity.javaClass.isAnnotationPresent(StopTrack::class.java)
            lifecycleReceiver.onLifecycleEventReceived(
                DataAnnotationClass(
                    activity.toTrackRequest(needToTrack),
                    activity.getTrackerParams()
                ),
                needToTrack
            )
        }
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)

        lifecycleReceiver.onLifecycleEventReceived(
            DataAnnotationClass(f.toTrackRequest(), f.getTrackerParams()),
            !f.javaClass.isAnnotationPresent(StopTrack::class.java)
        )
    }
}

/**
 * The implementation of [AppState]. This class listens to ONLY activity life cycles, and automatically creates a [TrackRequest] from the data coming from the life cycles.
 */
internal class ActivityAppStateImpl : AppState<DataAnnotationClass>() {

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)

        activity.let {
            val needToTrack = !activity.javaClass.isAnnotationPresent(StopTrack::class.java)
            lifecycleReceiver.onLifecycleEventReceived(
                DataAnnotationClass(
                    activity.toTrackRequest(needToTrack),
                    activity.getTrackerParams()
                ),
                needToTrack
            )
        }
    }
}

/**
 * The implementation of [AppState]. This class listens to ONLY fragment life cycles, and automatically creates a [TrackRequest] from the data coming from the life cycles.
 */
internal class FragmentStateImpl : AppState<DataAnnotationClass>() {

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)

        lifecycleReceiver.onLifecycleEventReceived(
            DataAnnotationClass(f.toTrackRequest(), f.getTrackerParams()),
            !f.javaClass.isAnnotationPresent(StopTrack::class.java)
        )
    }
}

internal class DisabledStateImpl : AppState<DataAnnotationClass>() {

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        activity.let {
            lifecycleReceiver.onLifecycleEventReceived(
                DataAnnotationClass(
                    it.toTrackRequest(false),
                    it.getTrackerParams()
                )
            )
        }
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        lifecycleReceiver.onLifecycleEventReceived(
            DataAnnotationClass(f.toTrackRequest(), f.getTrackerParams()), false
        )
    }
}