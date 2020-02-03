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
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.domain.InternalInteractor
import webtrekk.android.sdk.extension.toCustomParams

/**
 * Caching the created data and its custom param in the database.
 */
internal class CacheTrackForm(
    private val trackRequestRepository: TrackRequestRepository,
    private val customParamRepository: CustomParamRepository
) : InternalInteractor<CacheTrackForm.Params, DataTrack> {

    override suspend operator fun invoke(invokeParams: Params): Result<DataTrack> {
        return runCatching {
            // First, add the track request to the data base
            val cachedTrackRequest =
                trackRequestRepository.addTrackRequest(invokeParams.trackRequest)

            // Create the custom params with the id of the inserted track request
            val customParams =
                invokeParams.trackingParams.toCustomParams(cachedTrackRequest.getOrThrow().id)

            // insert the custom params in the data base
            val cachedCustomParams = customParamRepository.addCustomParams(customParams)

            DataTrack(
                cachedTrackRequest.getOrThrow(),
                cachedCustomParams.getOrThrow()
            )
        }
    }

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param trackRequest the track request that will be cached in the data base.
     * @param trackingParams the custom params that related to [trackRequest] that will be cached in the data base.
     */
    data class Params(val trackRequest: TrackRequest, val trackingParams: Map<String, String>)
}
