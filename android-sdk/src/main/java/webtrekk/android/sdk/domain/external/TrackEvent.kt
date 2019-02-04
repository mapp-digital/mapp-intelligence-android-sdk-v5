package webtrekk.android.sdk.domain.external

import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams

internal class TrackEvent(private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams) {

    // todo validate the params
    operator fun invoke(trackRequest: TrackRequest, trackingParams: Map<String, String>) {
        cacheTrackRequestWithCustomParams(trackRequest, trackingParams)
    }
}
