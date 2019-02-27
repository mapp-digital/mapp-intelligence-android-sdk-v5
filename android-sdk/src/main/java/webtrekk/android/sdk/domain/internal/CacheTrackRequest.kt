package webtrekk.android.sdk.domain.internal

import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.domain.InternalInteractor

internal class CacheTrackRequest(
    private val trackRequestRepository: TrackRequestRepository
) : InternalInteractor<CacheTrackRequest.Params, TrackRequest> {

    override suspend operator fun invoke(invokeParams: Params): Result<TrackRequest> {
        return trackRequestRepository.addTrackRequest(invokeParams.trackRequest)
    }

    data class Params(val trackRequest: TrackRequest)
}
