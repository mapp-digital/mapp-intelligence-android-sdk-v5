package webtrekk.android.sdk.domain.external

import kotlinx.coroutines.*
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import webtrekk.android.sdk.RequestType
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler

internal class TrackCustomEvent(
    coroutineDispatchers: CoroutineDispatchers,
    private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams
) : ExternalInteractor<TrackCustomEvent.Params> {

    private val _job = Job()
    override val scope = CoroutineScope(coroutineDispatchers.ioDispatcher + _job)

    override fun invoke(invokeParams: Params) {
        if (invokeParams.isOptOut) return

        scope.launch(coroutineExceptionHandler) {
            val params = invokeParams.trackingParams.toMutableMap()
            params[RequestType.EVENT.value] = invokeParams.trackRequest.name

            cacheTrackRequestWithCustomParams(
                CacheTrackRequestWithCustomParams.Params(
                    invokeParams.trackRequest,
                    params
                )
            )
                .onSuccess { logDebug("Cached the data track request: $it") }
                .onFailure { logError("Error while caching the request: $it") }
        }
    }

    data class Params(
        val trackRequest: TrackRequest,
        val trackingParams: Map<String, String>,
        val isOptOut: Boolean
    )
}
