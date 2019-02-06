package webtrekk.android.sdk

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.extension.toTrackRequest

internal class AppStateImpl : AppState<TrackRequest>() {

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)
        activity?.let { lifecycleReceiver.onEventReceived(activity.toTrackRequest()) }
    }

    override fun onActivityResumed(activity: Activity?) {
        super.onActivityResumed(activity)
        activity?.let { lifecycleReceiver.onEventReceived(activity.toTrackRequest()) }
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        lifecycleReceiver.onEventReceived(f.toTrackRequest())
    }
}
