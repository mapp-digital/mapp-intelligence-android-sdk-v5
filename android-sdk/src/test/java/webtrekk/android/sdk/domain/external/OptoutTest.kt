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
import io.mockk.Called
import io.mockk.coVerifyAll
import io.mockk.mockk
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import webtrekk.android.sdk.core.AppState
import webtrekk.android.sdk.core.Scheduler
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.data.entity.DataAnnotationClass
import webtrekk.android.sdk.domain.internal.ClearTrackRequests
import webtrekk.android.sdk.util.coroutinesDispatchersProvider

internal class OptoutTest : AbstractExternalInteractor() {

    private val sessions = mockk<Sessions>(relaxed = true)
    private val scheduler = mockk<Scheduler>(relaxed = true)
    private val appState = mockk<AppState<DataAnnotationClass>>(relaxed = true)
    private val clearTrackRequests = mockkClass(ClearTrackRequests::class)
    private val optOut = Optout(
        coroutineContext = coroutineContext,
        sessions = sessions,
        scheduler = scheduler,
        appState = appState,
        clearTrackRequests = clearTrackRequests
    )
    private val appContext = mockk<Context>(relaxed = true)

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        feature("set opt out value") {

            scenario("opt out and clear everything when setting optout to true") {
                val params =
                    Optout.Params(
                        context = appContext,
                        optOutValue = true,
                        sendCurrentData = false
                    )

                runBlocking {
                    optOut(params, coroutinesDispatchersProvider())

                    coVerifyAll {
                        sessions.optOut(params.optOutValue)
                        appState.disable(params.context)
                        scheduler.cancelScheduleSendRequests()
                        clearTrackRequests(ClearTrackRequests.Params(trackRequests = emptyList()))
                    }
                }
            }

            scenario("verify that if sendCurrentData is true while opting out then send all trackers before deleting") {
                val params =
                    Optout.Params(
                        context = appContext,
                        optOutValue = true,
                        sendCurrentData = true
                    )

                runBlocking {
                    optOut(params, coroutinesDispatchersProvider())

                    coVerifyAll {
                        sessions.optOut(params.optOutValue)
                        appState.disable(params.context)
                        scheduler.cancelScheduleSendRequests()
                        scheduler.sendRequestsThenCleanUp()
                        clearTrackRequests(ClearTrackRequests.Params(trackRequests = emptyList())) wasNot Called
                    }
                }
            }

            scenario("stop opting out when setting optout to false") {
                val params = Optout.Params(
                    context = appContext,
                    optOutValue = false,
                    sendCurrentData = false
                )

                runBlocking {
                    optOut(params, coroutinesDispatchersProvider())

                    coVerifyAll {
                        sessions.optOut(params.optOutValue)
                        clearTrackRequests(ClearTrackRequests.Params(trackRequests = emptyList())) wasNot Called
                    }
                }
            }
        }
    }
}
