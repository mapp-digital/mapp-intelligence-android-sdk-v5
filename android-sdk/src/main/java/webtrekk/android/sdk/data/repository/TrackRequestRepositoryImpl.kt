package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.data.entity.DataTrackView
import webtrekk.android.sdk.data.entity.TrackRequest

internal class TrackRequestRepositoryImpl(private val trackRequestDao: TrackRequestDao) :
    TrackRequestRepository {

    override suspend fun addTrackRequest(trackRequest: TrackRequest): Result<TrackRequest> {
        return runCatching {
            trackRequestDao.setTrackRequest(trackRequest).also { trackRequest.id = it }
                .run { trackRequest }
        }
    }

    override suspend fun getTrackRequests(): Result<List<DataTrackView>> {
        return runCatching {
            trackRequestDao.getTrackRequests()
        }
    }

    override suspend fun deleteTrackRequests(trackRequests: List<TrackRequest>): Result<Boolean> {
        return runCatching {
            trackRequestDao.clearTrackRequests(trackRequests).run { true }
        }
    }
}
