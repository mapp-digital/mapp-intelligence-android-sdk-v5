package webtrekk.android.sdk.domain.external

import webtrekk.android.sdk.data.model.TrackRequest
import webtrekk.android.sdk.domain.internal.AddTrackRequestWithCustomParams

internal class TrackEvent(private val addTrackRequestWithCustomParams: AddTrackRequestWithCustomParams) {

    operator fun invoke(trackRequest: TrackRequest, trackingParams: Map<String, String>) {
        addTrackRequestWithCustomParams(trackRequest, trackingParams)
    }
}
