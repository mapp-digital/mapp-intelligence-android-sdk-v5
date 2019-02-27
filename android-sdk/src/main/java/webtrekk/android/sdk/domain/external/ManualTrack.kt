package webtrekk.android.sdk.domain.external

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import webtrekk.android.sdk.logWarn
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler

internal class ManualTrack(
    coroutineDispatchers: CoroutineDispatchers,
    private val cacheTrackRequest: CacheTrackRequest,
    private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams
) : ExternalInteractor<ManualTrack.Params> {

    private val _job = Job()
    override val scope = CoroutineScope(coroutineDispatchers.ioDispatcher + _job)

    override operator fun invoke(invokeParams: Params) {
        if (invokeParams.isOptOut) return

        if (invokeParams.autoTrack) {
            logWarn("Auto track is enabled by default")

            return
        }

        scope.launch(coroutineExceptionHandler) {
            if (invokeParams.trackingParams.isEmpty()) {
                cacheTrackRequest(CacheTrackRequest.Params(invokeParams.trackRequest))
                    .onSuccess { logDebug("Cached the track request: $it") }
                    .onFailure { logError("Error while caching the request: $it") }
            } else {
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
    }

    data class Params(
        val trackRequest: TrackRequest,
        val trackingParams: Map<String, String>,
        val autoTrack: Boolean,
        val isOptOut: Boolean
    )
}
