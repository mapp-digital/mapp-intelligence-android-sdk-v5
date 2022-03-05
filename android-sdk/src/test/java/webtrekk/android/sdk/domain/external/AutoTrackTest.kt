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
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import webtrekk.android.sdk.core.AppState
import webtrekk.android.sdk.data.entity.DataAnnotationClass
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.util.cacheTrackRequestWithCustomParamsParams
import webtrekk.android.sdk.util.coroutinesDispatchersProvider

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AutoTrackTest {

    @RelaxedMockK
    lateinit var appState: AppState<DataAnnotationClass>

    @RelaxedMockK
    lateinit var appContext: Context

    @RelaxedMockK
    lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    lateinit var autoTrack: AutoTrack

    private val dispatcher = Dispatchers.Unconfined

    private val job = SupervisorJob()

    private val coroutineScope = CoroutineScope(dispatcher + job)

    @BeforeAll
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
        autoTrack =
            AutoTrack(coroutineScope.coroutineContext, appState, cacheTrackRequestWithCustomParams)
    }

    @Test
    fun `if opt out is active then return and don't track`() =  runTest {
        val params = AutoTrack.Params(context = appContext, isOptOut = true)

        autoTrack(params, coroutinesDispatchersProvider())

        coVerify(exactly = 0) {
            cacheTrackRequestWithCustomParams(cacheTrackRequestWithCustomParamsParams)
        }
    }

//    @Test
//    fun `verify that auto track received a track request and cached it`() = runTest {
//        val params = AutoTrack.Params(context = appContext, isOptOut = false)
//
//        // todo will fail since it's not possible to mock inline functions
//        every { appState.listenToLifeCycle(params.context, any()) } answers {
//            secondArg<(TrackRequest) -> Unit>().invoke(trackRequest)
//        }
//
//        autoTrack(params, coroutinesDispatchersProvider())
//
//        coVerifyAll {
//            cacheTrackRequestWithCustomParams(cacheTrackRequestWithCustomParamsParams)
//        }
//    }

    @AfterAll
    fun tearDown(){
        Dispatchers.resetMain()
        coroutineScope.cancel()
    }
}
