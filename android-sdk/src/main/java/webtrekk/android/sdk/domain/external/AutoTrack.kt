package webtrekk.android.sdk.domain.external

import android.content.Context
import webtrekk.android.sdk.AppState
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.logInfo

internal class AutoTrack(
    private val appState: AppState<TrackRequest>,
    private val cacheTrackRequest: CacheTrackRequest
) {

    operator fun invoke(context: Context) {
        appState.startAutoTrack(context) { trackRequest ->
            with(trackRequest) {
                logInfo("Received new auto track request: $this")

                cacheTrackRequest(this)
            }
        }
    }
}
