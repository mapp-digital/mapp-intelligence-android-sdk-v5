package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

internal interface TrackRequestRepository {

    suspend fun addTrackRequest(trackRequest: TrackRequest): Result<TrackRequest>
    suspend fun getTrackRequests(): Result<List<DataTrack>>
    suspend fun deleteTrackRequests(trackRequests: List<TrackRequest>): Result<Boolean>
}
