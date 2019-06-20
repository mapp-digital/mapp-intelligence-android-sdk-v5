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
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.util.trackRequests
import java.io.IOException

internal class ClearTrackRequestsTest : StringSpec({

    val trackRequestRepository = mockkClass(TrackRequestRepository::class)
    val clearTrackRequests = ClearTrackRequests(trackRequestRepository)

    "clear all track requests and return success" {
        val resultSuccess = Result.success(true)

        // We send emptyList of track requests to clear all track requests
        val emptyParams = ClearTrackRequests.Params(emptyList())

        coEvery {
            trackRequestRepository.deleteAllTrackRequests()
        } returns resultSuccess

        clearTrackRequests(emptyParams) shouldBe (resultSuccess)
    }

    "clear list of track requests and return success" {
        val resultSuccess = Result.success(true)

        val params = ClearTrackRequests.Params(trackRequests)

        coEvery {
            trackRequestRepository.deleteTrackRequests(trackRequests)
        } returns resultSuccess

        clearTrackRequests(params) shouldBe (resultSuccess)
    }

    "clear track requests and return failure" {
        val resultFailure = Result.failure<Boolean>(IOException("error"))

        val emptyParams = ClearTrackRequests.Params(emptyList())

        coEvery {
            trackRequestRepository.deleteAllTrackRequests()
        } returns resultFailure

        clearTrackRequests(emptyParams) shouldBe (resultFailure)
    }
})
