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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.inject
import webtrekk.android.sdk.core.AppState
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.core.CustomKoinComponent
import webtrekk.android.sdk.core.Scheduler
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.ClearTrackRequests
import kotlin.coroutines.CoroutineContext

/**
 * The opting out use case, opting out will stop the SDK from tracking data, clearing all the data in the data base, or send them first then clean up, and canceling all work manager workers.
 */
internal class Optout(
    coroutineContext: CoroutineContext,
    private val sessions: Sessions,
    private val scheduler: Scheduler,
    private val appState: AppState<TrackRequest>,
    private val clearTrackRequests: ClearTrackRequests
) : ExternalInteractor<Optout.Params>, CustomKoinComponent {

    private val _job = Job()
    override val scope =
        CoroutineScope(_job + coroutineContext) // Starting a new job with context of the parent.

    /**
     * [logger] the injected logger from Webtrekk.
     */
    private val logger by inject<Logger>()

    override fun invoke(invokeParams: Params, coroutineDispatchers: CoroutineDispatchers) {
        // Store the opt out value in the shared preferences.
        sessions.optOut(invokeParams.optOutValue)

        // If opt out value is set to true, then disable tracking data, cancel all work manager workers and detete or send then delete current data in the data base.
        if (invokeParams.optOutValue) {
            appState.disable(invokeParams.context) // Disable the auto track
            scheduler.cancelScheduleSendRequests() // Cancel the work manager workers

            // If sendCurrentData is true, then one time worker will send current data requests to the server, then clean up the data base.
            if (invokeParams.sendCurrentData) {
                scheduler.sendRequestsThenCleanUp()
            } else {
                scope.launch(
                    context = coroutineDispatchers.ioDispatcher + coroutineExceptionHandler(
                        logger
                    ),
                    start = CoroutineStart.ATOMIC
                ) {
                    clearTrackRequests(ClearTrackRequests.Params(trackRequests = emptyList()))
                        .onSuccess { logger.debug("Cleared all track requests, opt out is active") }
                        .onFailure { logger.error("Failed to clear the track requests while opting out") }
                }
            }
        }
    }

    fun isActive(): Boolean = sessions.isOptOut()

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param context the application context.
     * @param optOutValue the opt out value.
     * @param sendCurrentData the flag of sending the current cached data in the data base before opting out or not.
     */
    data class Params(
        val context: Context,
        val optOutValue: Boolean,
        val sendCurrentData: Boolean
    )
}
