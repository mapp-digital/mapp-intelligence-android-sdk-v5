package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.data.model.DataTrackView
import webtrekk.android.sdk.data.model.TrackRequest
import webtrekk.android.sdk.data.tryToQuery

internal class TrackRequestRepositoryImpl(private val trackRequestDao: TrackRequestDao) :
    TrackRequestRepository {

    override suspend fun addTrackRequest(trackRequest: TrackRequest): DataResult<TrackRequest> {
        return tryToQuery(
            {
                trackRequestDao.setTrackRequest(trackRequest).also { trackRequest.id = it }
                DataResult.Success(trackRequest)
            }, "Failed to add a new track request to webtrekk database"
        )
    }

    override suspend fun getTrackRequests(): DataResult<List<DataTrackView>> {
        return tryToQuery(
            {
                val data = trackRequestDao.getTrackRequests()
                DataResult.Success(data)
            }, "Failed to get track requests from webtrekk database"
        )
    }

    override suspend fun deleteTrackRequests(trackRequests: List<TrackRequest>): DataResult<Boolean> {
        return tryToQuery(
            {
                trackRequestDao.clearTrackRequests(trackRequests)
                DataResult.Success(true)
            }, "Failed to clear the track requests"
        )
    }
}
