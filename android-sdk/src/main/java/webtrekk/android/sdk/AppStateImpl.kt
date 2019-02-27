package webtrekk.android.sdk

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.extension.toTrackRequest

internal class AppStateImpl : AppState<TrackRequest>() {

    override fun onActivityStarted(activity: Activity?) {
        super.onActivityStarted(activity)

        activity?.let { lifecycleReceiver.onLifecycleEventReceived(activity.toTrackRequest()) }
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)

        lifecycleReceiver.onLifecycleEventReceived(f.toTrackRequest())
    }
}
