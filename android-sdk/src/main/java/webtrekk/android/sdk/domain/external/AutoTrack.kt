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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.inject
import webtrekk.android.sdk.core.AppState
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.core.CustomKoinComponent
import webtrekk.android.sdk.data.entity.DataAnnotationClass
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.extension.toParam
import webtrekk.android.sdk.integration.IntelligenceEvent
import webtrekk.android.sdk.integration.MappIntelligenceListener.Companion.PAGE
import kotlin.coroutines.CoroutineContext

/**
 * The auto track use case. The auto track listens to activity and/or fragments life cycles, and at each event coming from life cycle, will be inserted in the data base.
 */
internal class AutoTrack(
    coroutineContext: CoroutineContext,
    private val appState: AppState<DataAnnotationClass>,
    private val cacheTrackRequest: CacheTrackRequestWithCustomParams
) : ExternalInteractor<AutoTrack.Params>, CustomKoinComponent {

    private val _job = Job()
    override val scope =
        CoroutineScope(_job + coroutineContext) // Starting a new job with context of the parent.

    /**
     * [logger] the injected logger from Webtrekk.
     */
    private val logger by inject<Logger>()

    override operator fun invoke(invokeParams: Params, coroutineDispatchers: CoroutineDispatchers) {
        // If opt out is active, then return

        if (invokeParams.isOptOut) return

        // Listen to the life cycle listeners, and cache the data
        appState.listenToLifeCycle(invokeParams.context) { trackRequest ->
            logger.info("Received a request from auto track: ${trackRequest.trackRequest}")
            scope.launch(
                coroutineDispatchers.ioDispatcher + coroutineExceptionHandler(
                    logger
                )
            ) {
                cacheTrackRequest(
                    CacheTrackRequestWithCustomParams.Params(
                        trackRequest.trackRequest,
                        trackRequest.trackParams.toParam()
                    )
                )
                    .onSuccess { logger.debug("Cached auto track request: $it") }
                    .onFailure { logger.error("Error while caching auto track request: $it") }
            }
        }
    }

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param [context] the application context, which will be used to start the auto track listeners.
     * @param [isOptOut] the value of the opt out.
     */
    data class Params(val context: Context, val isOptOut: Boolean)
}
