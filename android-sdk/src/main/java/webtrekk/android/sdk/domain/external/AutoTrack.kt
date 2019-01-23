package webtrekk.android.sdk.domain.external

import android.content.Context
import webtrekk.android.sdk.AppState
import webtrekk.android.sdk.data.model.TrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.logInfo
import webtrekk.android.sdk.util.resolution

internal class AutoTrack(
    private val appState: AppState<TrackRequest>,
    private val cacheTrackRequest: CacheTrackRequest
) {

    operator fun invoke(context: Context) {
        appState.startAutoTrack(context) { trackRequest ->
            with(trackRequest) {
                logInfo("Received new auto track request: $this")

                screenResolution = context.resolution()

                cacheTrackRequest(this)
            }
        }
    }
}
