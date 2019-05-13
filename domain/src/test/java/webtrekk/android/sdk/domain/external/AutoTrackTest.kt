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

import android.content.Context
import io.kotlintest.IsolationMode
import io.mockk.mockk
import io.mockk.mockkClass
import webtrekk.android.sdk.core.AppState
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequest

internal class AutoTrackTest : AbstractExternalInteractor() {

    private val appState: AppState<TrackRequest> = mockk(relaxed = true)
    private val cacheTrackRequest = mockkClass(CacheTrackRequest::class)
    private val appContext = mockk<Context>(relaxed = true)
    private val autoTrack = AutoTrack(coroutineContext, appState, cacheTrackRequest)

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        feature("auto track is enabled") {

//            scenario("if opt out is active then return and don't track") {
//                val params = AutoTrack.Params(context = appContext, isOptOut = true)
//
//                runBlocking {
//                    autoTrack(params, coroutinesDispatchersProvider())
//
//                    coVerify {
//                        cacheTrackRequest(cacheTrackRequestParams) wasNot Called
//                    }
//                }
//            }
//
//            scenario("verify that auto track received a track request and cached it") {
//                val params = AutoTrack.Params(context = appContext, isOptOut = false)
//
//                // todo will fail since it's not possible to mock inline functions
//                every { appState.listenToLifeCycle(params.context, any()) } answers {
//                    secondArg<(TrackRequest) -> Unit>().invoke(trackRequest)
//                }
//
//                runBlocking {
//                    autoTrack(params, coroutinesDispatchersProvider())
//
//                    coVerifyAll {
//                        cacheTrackRequest(cacheTrackRequestParams)
//                    }
//                }
//            }
        }
    }
}
