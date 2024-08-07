/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

/**
 * A concrete implementation of [TrackRequestRepository] that depends on Room database.
 */
internal class TrackRequestRepositoryImpl(private val trackRequestDao: TrackRequestDao) :
    TrackRequestRepository {

    override suspend fun updateTrackRequests(trackRequests: List<TrackRequest>): Result<List<TrackRequest>> {
        return runCatching {
            trackRequestDao.updateTrackRequests(trackRequests)
            trackRequests
        }
    }

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
            trackRequestDao.clearTrackRequests(trackRequests)
            true
        }
    }

    override suspend fun deleteAllTrackRequests(): Result<Boolean> {
        return runCatching {
            trackRequestDao.clearAllTrackRequests()
            true
        }
    }

    override suspend fun updateEverId(everId: String?) {
        runCatching {
            trackRequestDao.updateEverId(everId)
        }
    }
}
