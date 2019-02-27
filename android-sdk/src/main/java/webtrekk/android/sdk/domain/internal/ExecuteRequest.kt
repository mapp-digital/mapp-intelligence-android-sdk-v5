package webtrekk.android.sdk.domain.internal

import okhttp3.*
import webtrekk.android.sdk.api.SyncRequestsDataSource
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.domain.InternalInteractor
import webtrekk.android.sdk.logInfo

internal class ExecuteRequest(
    private val trackRequestRepository: TrackRequestRepository,
    private val syncRequestsDataSource: SyncRequestsDataSource<DataTrack>
) : InternalInteractor<ExecuteRequest.Params, DataTrack> {

    override suspend operator fun invoke(invokeParams: Params): Result<DataTrack> {
        return with(invokeParams) {
            logInfo("Sending request = $request")

            // send the request and update its state if the request sent successfully or failed.
            syncRequestsDataSource.sendRequest(request, dataTrack)
                .onSuccess {
                    it.trackRequest.requestState = TrackRequest.RequestState.DONE
                    trackRequestRepository.updateTrackRequests(it.trackRequest)
                }
                .onFailure {
                    dataTrack.trackRequest.requestState = TrackRequest.RequestState.FAILED
                    trackRequestRepository.updateTrackRequests(dataTrack.trackRequest)
                }
        }

    }

    data class Params(val request: Request, val dataTrack: DataTrack)
}
