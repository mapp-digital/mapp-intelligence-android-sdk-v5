package webtrekk.android.sdk.domain.external

import android.content.Context
import webtrekk.android.sdk.AppState
import webtrekk.android.sdk.data.model.TrackRequest
import webtrekk.android.sdk.domain.internal.AddTrackRequest
import webtrekk.android.sdk.util.resolution

internal class AutoTrack(
    private val appState: AppState<TrackRequest>,
    private val addTrackRequest: AddTrackRequest
) {

    operator fun invoke(context: Context) {
        appState.startAutoTrack(context) { trackRequest ->
            with(trackRequest) {
                screenResolution = context.resolution()
                val result = addTrackRequest(this)
            }
        }
    }
}
