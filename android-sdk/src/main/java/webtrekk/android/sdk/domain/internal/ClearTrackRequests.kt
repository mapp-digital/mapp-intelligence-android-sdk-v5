package webtrekk.android.sdk.domain.internal

import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.domain.InternalInteractor

internal class ClearTrackRequests(
    private val trackRequestRepository: TrackRequestRepository
) : InternalInteractor<ClearTrackRequests.Params, Boolean> {

    override suspend operator fun invoke(invokeParams: Params): Result<Boolean> {
        return if (invokeParams.trackRequests.isEmpty()) {
            trackRequestRepository.deleteAllTrackRequests()
        } else {
            trackRequestRepository.deleteTrackRequests(invokeParams.trackRequests)
        }
    }

    data class Params(val trackRequests: List<TrackRequest>)
}
