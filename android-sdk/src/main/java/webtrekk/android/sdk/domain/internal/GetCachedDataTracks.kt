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

package webtrekk.android.sdk.domain.internal

import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.domain.InternalInteractor

/**
 * Get all the cached data tracks from the data base.
 */
internal open class GetCachedDataTracks(
    private val trackRequestRepository: TrackRequestRepository
) : InternalInteractor<GetCachedDataTracks.Params, List<DataTrack>> {

    override suspend operator fun invoke(invokeParams: Params): Result<List<DataTrack>> {
        // If the track request state list is empty, the get all the data in the data base. otherwise, just get the data that are related to the specific list of states.
        return if (invokeParams.requestStates.isEmpty()) {
            trackRequestRepository.getTrackRequests()
        } else {
            trackRequestRepository.getTrackRequestsByState(invokeParams.requestStates)
        }
    }

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param [requestStates] list of the track requests state that will retrieve the track requests from the data base based on those states.
     */
    data class Params(val requestStates: List<TrackRequest.RequestState>)
}
