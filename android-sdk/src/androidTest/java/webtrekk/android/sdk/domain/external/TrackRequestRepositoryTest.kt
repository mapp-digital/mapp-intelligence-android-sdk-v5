package webtrekk.android.sdk.domain.external

import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository

internal class TrackRequestRepositoryTest : TrackRequestRepository {
    private var everId: String? = null
    private val requests = mutableListOf<TrackRequest>()
    override suspend fun addTrackRequest(trackRequest: TrackRequest): Result<TrackRequest> {
        requests.add(trackRequest)
        return Result.success(trackRequest)
    }

    override suspend fun getTrackRequests(): Result<List<DataTrack>> {
        val data = requests.map { DataTrack(it, emptyList()) }
        return Result.success(data)
    }

    override suspend fun getTrackRequestsByState(requestStates: List<TrackRequest.RequestState>): Result<List<DataTrack>> {
        val data = requests
            .filter { it.requestState in requestStates }
            .map { DataTrack(it, emptyList()) }
        return Result.success(data)
    }

    override suspend fun updateTrackRequests(vararg trackRequests: TrackRequest): Result<List<TrackRequest>> {
        requests.addAll(trackRequests)
        return Result.success(requests.toMutableList())
    }

    override suspend fun updateTrackRequests(trackRequests: List<TrackRequest>): Result<List<TrackRequest>> {
        requests.addAll(trackRequests)
        return Result.success(requests.toMutableList())
    }

    override suspend fun deleteTrackRequests(trackRequests: List<TrackRequest>): Result<Boolean> {
        trackRequests.toMutableList()
            .filter { it.id !in trackRequests.map { it.id } }
            .let {
                requests.clear()
                requests.addAll(it)
            }
        return Result.success(true)
    }

    override suspend fun deleteAllTrackRequests(): Result<Boolean> {
        requests.clear()
        return Result.success(true)
    }

    override suspend fun updateEverId(everId: String?) {
        this.everId = everId
    }
}