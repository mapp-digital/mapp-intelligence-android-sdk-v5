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
import kotlinx.coroutines.*
import webtrekk.android.sdk.AppState
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.ClearTrackRequests
import webtrekk.android.sdk.domain.Scheduler
import webtrekk.android.sdk.domain.Sessions
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

internal class Optout(
    coroutineContext: CoroutineContext,
    private val sessions: Sessions,
    private val scheduler: Scheduler,
    private val appState: AppState<TrackRequest>,
    private val clearTrackRequests: ClearTrackRequests
) : ExternalInteractor<Optout.Params> {

    private val _job = Job()
    override val scope = CoroutineScope(_job + coroutineContext)

    override fun invoke(invokeParams: Params, coroutineDispatchers: CoroutineDispatchers) {
        sessions.optOut(invokeParams.optOutValue)

        if (invokeParams.optOutValue) {
            appState.disableAutoTrack(invokeParams.context)
            scheduler.cancelSendRequests()

            scope.launch(
                context = coroutineDispatchers.ioDispatcher + coroutineExceptionHandler,
                start = CoroutineStart.ATOMIC
            ) {
                clearTrackRequests(ClearTrackRequests.Params(trackRequests = emptyList()))
                    .onSuccess { logDebug("Cleared all track requests") }
                    .onFailure { logError("Failed to clear the track requests while opting out") }
            }
        }
    }

    fun isActive(): Boolean = sessions.isOptOut()

    data class Params(val context: Context, val optOutValue: Boolean)
}
