package webtrekk.android.sdk.domain.external

import webtrekk.android.sdk.data.model.TrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams

internal class TrackPage(private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams) {

    operator fun invoke(trackRequest: TrackRequest, trackingParams: Map<String, String>) {
        cacheTrackRequestWithCustomParams(trackRequest, trackingParams)
    }
}
