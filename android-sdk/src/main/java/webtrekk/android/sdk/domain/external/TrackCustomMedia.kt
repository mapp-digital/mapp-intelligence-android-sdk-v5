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
import webtrekk.android.sdk.api.RequestType
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.integration.IntelligenceEvent
import webtrekk.android.sdk.integration.MappIntelligenceListener
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * Track media event. This  interface is used only for tracking media event .
 */
internal class TrackCustomMedia(
    coroutineContext: CoroutineContext,
    private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams
) : ExternalInteractor<TrackCustomMedia.Params> {

    private val _job = Job()
    override val scope =
        CoroutineScope(_job + coroutineContext) // Starting a new job with context of the parent.

    /**
     * [logger] the injected logger from Webtrekk.
     */
    private val logger by lazy { AppModule.logger }

    override operator fun invoke(invokeParams: Params, coroutineDispatchers: CoroutineDispatchers) {
        // If opt out is active, then return
        IntelligenceEvent.sendEvent(
            invokeParams.context,
            MappIntelligenceListener.MEDIA, invokeParams.trackRequest.name
        )
        if (invokeParams.isOptOut) return

        scope.launch(
            coroutineDispatchers.ioDispatcher + coroutineExceptionHandler(
                logger
            )
        ) {
            val params = invokeParams.trackingParams.toMutableMap()
            params[RequestType.MEDIA.value] = invokeParams.trackRequest.name
            params.remove(RequestType.EVENT.value)
            // Cache the track request with its custom params.
            cacheTrackRequestWithCustomParams(
                CacheTrackRequestWithCustomParams.Params(
                    invokeParams.trackRequest,
                    params
                )
            )
                .onSuccess { logger.debug("Cached custom media request: $it") }
                .onFailure { logger.error("Error while caching custom media request: $it") }
        }
    }

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param trackRequest the track request that is created and will be cached in the data base.
     * @param trackingParams the custom params associated with the [trackRequest].
     * @param isOptOut the opt out value.
     */
    data class Params(
        val trackRequest: TrackRequest,
        val trackingParams: Map<String, String>,
        val isOptOut: Boolean,
        val context: Context? = null
    )
}
