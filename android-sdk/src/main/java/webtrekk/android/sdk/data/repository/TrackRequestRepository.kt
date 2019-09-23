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

import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

/**
 * A repository interface that represents the [TrackRequest] operations in the data layer. The domain layer interacts ONLY with repositories interfaces and not directly with Room.
 *
 * All the methods are suspendable and use Coroutines under the hood for the I/O operations.
 * All the methods return the object encapsulated in a [Result], and handling the success or the failure of the results are done in the domain layer.
 */
internal interface TrackRequestRepository {

    /**
     * Returns the [TrackRequest] encapsulated in a [Result] indicating if the [trackRequest] is added in the database or not.
     *
     * @param trackRequest
     */
    suspend fun addTrackRequest(trackRequest: TrackRequest): Result<TrackRequest>

    /**
     * Returns list of [DataTrack] from the database encapsulated in a [Result].
     */
    suspend fun getTrackRequests(): Result<List<DataTrack>>

    /**
     * Returns list of [DataTrack] based on the track request state [TrackRequest.RequestState] encapsulated in a [Result].
     *
     * @param requestStates list of track request states.
     */
    suspend fun getTrackRequestsByState(requestStates: List<TrackRequest.RequestState>): Result<List<DataTrack>>

    /**
     * Returns list of [TrackRequest] with the updating new states encapsulated in a [Result].
     *
     * @param trackRequests that have new states.
     */
    suspend fun updateTrackRequests(vararg trackRequests: TrackRequest): Result<List<TrackRequest>>

    suspend fun updateTrackRequests(trackRequests: List<TrackRequest>): Result<List<TrackRequest>>

    /**
     * Returns true if list of [TrackRequest] are deleted from the database, false otherwise, encapsulated in a [Result].
     *
     * @param trackRequests list of [TrackRequest] that need to be deleted from the database.
     */
    suspend fun deleteTrackRequests(trackRequests: List<TrackRequest>): Result<Boolean>

    /**
     * Returns true if all [TrackRequest] are deleted from the database, false otherwise, encapsulated in a [Result].
     */
    suspend fun deleteAllTrackRequests(): Result<Boolean>
}
