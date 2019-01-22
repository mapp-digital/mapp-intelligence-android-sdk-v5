package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.model.DataTrack
import webtrekk.android.sdk.data.model.DataTrackView
import webtrekk.android.sdk.data.model.TrackRequest

internal interface TrackRequestRepository {

    suspend fun addTrackRequest(trackRequest: TrackRequest): DataResult<TrackRequest>
    suspend fun getTrackRequests(): DataResult<List<DataTrack>>
    suspend fun deleteTrackRequests(trackRequests: List<TrackRequest>): DataResult<Boolean>
}
