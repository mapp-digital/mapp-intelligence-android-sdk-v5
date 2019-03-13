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

import buildUrlRequestForTesting
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.mockkClass
import webtrekk.android.sdk.api.SyncRequestsDataSourceImpl
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.util.dataTrack

internal class ExecuteRequestTest : StringSpec({

    val trackRequestRepository = mockkClass(TrackRequestRepository::class)
    val syncRequestDataSource = mockkClass(SyncRequestsDataSourceImpl::class)
    val executeRequest = ExecuteRequest(trackRequestRepository, syncRequestDataSource)

    val urlRequest = dataTrack.buildUrlRequestForTesting("https://www.webtrekk.com", listOf("123"))
    val params = ExecuteRequest.Params(request = urlRequest, dataTrack = dataTrack)

    "execute the request then update its track request's state" {
        val resultSuccess = Result.success(dataTrack)

        coEvery {
            syncRequestDataSource.sendRequest(urlRequest, dataTrack)
        } returns resultSuccess

        val syncRequestSuccess = syncRequestDataSource.sendRequest(urlRequest, dataTrack)

        if (syncRequestSuccess.isSuccess) {
            val updatedTrackRequest = syncRequestSuccess.getOrThrow().trackRequest
            updatedTrackRequest.requestState = TrackRequest.RequestState.DONE

            val updatedTrackRequestSuccess = Result.success(listOf(updatedTrackRequest))

            coEvery {
                trackRequestRepository.updateTrackRequests(updatedTrackRequest)
            } returns updatedTrackRequestSuccess

            executeRequest(params) shouldBe (resultSuccess)
        }
    }
})
