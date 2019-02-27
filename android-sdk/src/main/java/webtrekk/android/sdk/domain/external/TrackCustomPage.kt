package webtrekk.android.sdk.domain.external

import kotlinx.coroutines.*
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler

internal class TrackCustomPage(
    coroutineDispatchers: CoroutineDispatchers,
    private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams
) : ExternalInteractor<TrackCustomPage.Params> {

    private val _job = Job()
    override val scope = CoroutineScope(coroutineDispatchers.ioDispatcher + _job)

    override operator fun invoke(invokeParams: Params) {
        if (invokeParams.isOptOut) return

        scope.launch(coroutineExceptionHandler) {
            cacheTrackRequestWithCustomParams(
                CacheTrackRequestWithCustomParams.Params(
                    invokeParams.trackRequest,
                    invokeParams.trackingParams
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
