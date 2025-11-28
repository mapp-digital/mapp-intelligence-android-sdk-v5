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

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.core.AppState
import webtrekk.android.sdk.core.Scheduler
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.data.entity.DataAnnotationClass
import webtrekk.android.sdk.domain.internal.ClearTrackRequests
import webtrekk.android.sdk.util.coroutinesDispatchersProvider

@OptIn(ExperimentalCoroutinesApi::class)
internal class OptoutTest : BaseExternalTest() {
    @RelaxedMockK
    lateinit var sessions: Sessions

    @RelaxedMockK
    lateinit var scheduler: Scheduler

    @RelaxedMockK
    lateinit var appState: AppState<DataAnnotationClass>

    @MockK
    lateinit var clearTrackRequests: ClearTrackRequests

    lateinit var optOut: Optout

    @Before
    override fun setup() {
        super.setup()
        MockKAnnotations.init(this, relaxUnitFun = true)
        optOut = Optout(
            coroutineContext = coroutineScope.coroutineContext,
            sessions = sessions,
            scheduler = scheduler,
            appState = appState,
            clearTrackRequests = clearTrackRequests
        )
    }

    @Test
    fun `opt out and clear everything when setting optout to true`() = runTest {
        val params =
            Optout.Params(
                context = appContext,
                optOutValue = true,
                sendCurrentData = false
            )

        optOut(params, coroutinesDispatchersProvider())

        coVerifyOrder {
            sessions.optOut(params.optOutValue)
            appState.disable(params.context)
            scheduler.cancelScheduleSendRequests()
            clearTrackRequests(ClearTrackRequests.Params(trackRequests = emptyList()))
        }
    }

    @Test
    fun `verify that if sendCurrentData is true while opting out then send all trackers before deleting`() =
        runTest {
            val params =
                Optout.Params(
                    context = appContext,
                    optOutValue = true,
                    sendCurrentData = true
                )

            optOut(params, coroutinesDispatchersProvider())

            coVerify {
                sessions.optOut(params.optOutValue)
                appState.disable(params.context)
                scheduler.cancelScheduleSendRequests()
                scheduler.sendRequestsThenCleanUp()
            }
        }

    @Test
    fun `stop opting out when setting optout to false`() = runTest {
        val params = Optout.Params(
            context = appContext,
            optOutValue = false,
            sendCurrentData = false
        )

        optOut(params, coroutinesDispatchersProvider())

        coVerify(exactly = 1) {
            sessions.optOut(params.optOutValue)
        }
        assertThat(params.optOutValue).isFalse()
    }
}
