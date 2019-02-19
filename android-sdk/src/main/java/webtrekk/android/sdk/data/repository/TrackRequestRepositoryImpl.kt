package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

internal class TrackRequestRepositoryImpl(private val trackRequestDao: TrackRequestDao) :
    TrackRequestRepository {

    override suspend fun addTrackRequest(trackRequest: TrackRequest): Result<TrackRequest> {
        return runCatching {
            trackRequestDao.setTrackRequest(trackRequest).also { trackRequest.id = it }
                .run { trackRequest }
        }
    }

    override suspend fun getTrackRequests(): Result<List<DataTrack>> {
        return runCatching {
            trackRequestDao.getTrackRequests()
        }
    }

    override suspend fun getTrackRequestsByState(requestStates: List<TrackRequest.RequestState>): Result<List<DataTrack>> {
        return runCatching {
            trackRequestDao.getTrackRequestsByState(requestStates.map { it.value })
        }
    }

    override suspend fun updateTrackRequests(vararg trackRequests: TrackRequest): Result<List<TrackRequest>> {
        return runCatching {
            trackRequestDao.updateTrackRequests(*trackRequests)
            trackRequests.toList()
        }
    }

    override suspend fun deleteTrackRequests(trackRequests: List<TrackRequest>): Result<Boolean> {
        return runCatching {
            trackRequestDao.clearTrackRequests(trackRequests).run { true }
        }
    }
}
