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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.api.RequestType
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import kotlin.coroutines.CoroutineContext

internal class TrackCustomEvent(
    coroutineContext: CoroutineContext,
    private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams
) : ExternalInteractor<TrackCustomEvent.Params>, KoinComponent {

    private val _job = Job()
    override val scope = CoroutineScope(_job + coroutineContext)

    private val logger by inject<Logger>()

    override fun invoke(invokeParams: Params, coroutineDispatchers: CoroutineDispatchers) {
        if (invokeParams.isOptOut) return

        scope.launch(coroutineDispatchers.ioDispatcher + coroutineExceptionHandler(
            logger
        )
        ) {
            val params = invokeParams.trackingParams.toMutableMap()
            params[RequestType.EVENT.value] = invokeParams.trackRequest.name

            cacheTrackRequestWithCustomParams(
                CacheTrackRequestWithCustomParams.Params(
                    invokeParams.trackRequest,
                    params
                )
            )
                .onSuccess { logger.debug("Cached custom event request: $it") }
                .onFailure { logger.error("Error while caching custom event request: $it") }
        }
    }

    data class Params(
        val trackRequest: TrackRequest,
        val trackingParams: Map<String, String>,
        val isOptOut: Boolean
    )
}
