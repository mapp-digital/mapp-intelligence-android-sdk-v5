package webtrekk.android.sdk.domain.external

import android.content.Context
import kotlinx.coroutines.*
import webtrekk.android.sdk.*
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler

internal class AutoTrack(
    coroutineDispatchers: CoroutineDispatchers,
    private val appState: AppState<TrackRequest>,
    private val cacheTrackRequest: CacheTrackRequest
) : ExternalInteractor<AutoTrack.Params> {

    private val _job = Job()
    override val scope = CoroutineScope(coroutineDispatchers.ioDispatcher + _job)

    override operator fun invoke(invokeParams: Params) {
        if (invokeParams.isOptOut) return

        appState.startAutoTrack(invokeParams.context) { trackRequest ->
            logInfo("Received new auto track request: $trackRequest")

            scope.launch(coroutineExceptionHandler) {
                cacheTrackRequest(CacheTrackRequest.Params(trackRequest))
                    .onSuccess { logDebug("Cached auto track request: $it") }
                    .onFailure { logError("Error while caching the request: $it") }
            }
        }
    }

    data class Params(val context: Context, val isOptOut: Boolean)
}
