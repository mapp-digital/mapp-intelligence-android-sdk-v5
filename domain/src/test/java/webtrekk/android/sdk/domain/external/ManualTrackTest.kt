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

package webtrekk.android.sdk.domain.external

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.domain.util.coroutinesDispatchersProvider
import webtrekk.android.sdk.domain.util.trackingParams
import webtrekk.android.sdk.domain.util.cacheTrackRequestParams
import webtrekk.android.sdk.domain.util.cacheTrackRequestWithCustomParamsParams
import webtrekk.android.sdk.domain.util.dataTrack
import webtrekk.android.sdk.domain.util.trackRequest

internal class ManualTrackTest : AbstractExternalInteractor() {

    val cacheTrackRequest = mockkClass(CacheTrackRequest::class)
    val cacheTrackRequestWithCustomParams = mockkClass(CacheTrackRequestWithCustomParams::class)
    val manualTrack: ManualTrack = ManualTrack(
        coroutineContext,
        cacheTrackRequest,
        cacheTrackRequestWithCustomParams
    )

    init {
        feature("the manual track is called") {

            scenario("if opt out is active or auto tracking is enabled then return and don't track") {
                val params = ManualTrack.Params(trackRequest, emptyMap(), true, true)

                runBlocking {
                    manualTrack(params, coroutinesDispatchersProvider())

                    coVerifyAll {
                        cacheTrackRequest(cacheTrackRequestParams) wasNot Called
                        cacheTrackRequestWithCustomParams wasNot Called
                    }
                }
            }

            scenario("verify that cacheTrackRequest is called when there is no tracking params attached") {
                val result = Result.success(trackRequest)

                coEvery { cacheTrackRequest(cacheTrackRequestParams) } returns result

                val params = ManualTrack.Params(trackRequest, emptyMap(), false, false)

                runBlocking {
                    manualTrack(params, coroutinesDispatchersProvider())

                    coVerifyAll {
                        cacheTrackRequest(cacheTrackRequestParams)
                    }
                }
            }

            scenario("verify that cacheTrackRequestWithCustomParams is called when there are tracking params") {
                val resultSuccess = Result.success(dataTrack)

                coEvery { cacheTrackRequestWithCustomParams(cacheTrackRequestWithCustomParamsParams) } returns resultSuccess

                val params = ManualTrack.Params(trackRequest, trackingParams, false, false)

                runBlocking {
                    manualTrack(params, coroutinesDispatchersProvider())

                    coVerifyAll {
                        cacheTrackRequestWithCustomParams(cacheTrackRequestWithCustomParamsParams)
                    }
                }
            }
        }
    }
}
