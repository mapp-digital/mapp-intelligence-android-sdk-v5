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

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.mockkClass
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.util.dataTracks
import java.io.IOException

internal class GetCachedDataTracksTest : StringSpec({

    val trackRequestRepository = mockkClass(TrackRequestRepository::class)
    val getCachedDataTracks =
        GetCachedDataTracks(trackRequestRepository)

    "get all data tracks and return success of their results" {
        val resultSuccess = Result.success(dataTracks)

        // We send emptyList of States to get all the track requests
        val emptyParams = GetCachedDataTracks.Params(emptyList())

        coEvery {
            trackRequestRepository.getTrackRequests()
        } returns resultSuccess

        getCachedDataTracks(emptyParams) shouldBe (resultSuccess)
    }

    "get data tracks by request states and return success of their results" {
        val requestStates = listOf(TrackRequest.RequestState.NEW, TrackRequest.RequestState.FAILED)
        val params = GetCachedDataTracks.Params(requestStates)

        // Filter the results by the request states
        val resultSuccess = Result.success(dataTracks.filter {
            it.trackRequest.requestState == TrackRequest.RequestState.NEW ||
                it.trackRequest.requestState == TrackRequest.RequestState.FAILED
        })

        coEvery {
            trackRequestRepository.getTrackRequestsByState(requestStates)
        } returns resultSuccess

        getCachedDataTracks(params) shouldBe (resultSuccess)
    }

    "get data tracks and return failure" {
        val resultFailure = Result.failure<List<DataTrack>>(IOException("error"))

        // We send emptyList of States to get all the track requests
        val emptyParams = GetCachedDataTracks.Params(emptyList())

        coEvery {
            trackRequestRepository.getTrackRequests()
        } returns resultFailure

        getCachedDataTracks(emptyParams) shouldBe (resultFailure)
    }
})
