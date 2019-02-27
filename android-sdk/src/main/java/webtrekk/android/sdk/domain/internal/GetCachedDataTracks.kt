package webtrekk.android.sdk.domain.internal

import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.domain.InternalInteractor

internal class GetCachedDataTracks(
    private val trackRequestRepository: TrackRequestRepository
) : InternalInteractor<GetCachedDataTracks.Params, List<DataTrack>> {

    override suspend operator fun invoke(invokeParams: Params): Result<List<DataTrack>> {
        return if (invokeParams.requestStates.isEmpty()) {
            trackRequestRepository.getTrackRequests()
        } else {
            trackRequestRepository.getTrackRequestsByState(invokeParams.requestStates)
        }
    }

    data class Params(val requestStates: List<TrackRequest.RequestState>)
}
