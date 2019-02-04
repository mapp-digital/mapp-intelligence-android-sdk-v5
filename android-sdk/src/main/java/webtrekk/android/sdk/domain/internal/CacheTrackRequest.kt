package webtrekk.android.sdk.domain.internal

import kotlinx.coroutines.*
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import kotlin.coroutines.CoroutineContext

internal class CacheTrackRequest(
    private val trackRequestRepository: TrackRequestRepository,
    coroutineContext: CoroutineContext
) {

    private val scope = CoroutineScope(coroutineContext + Dispatchers.IO)

    operator fun invoke(trackRequest: TrackRequest) = scope.async {
        trackRequestRepository.addTrackRequest(trackRequest)
            .onSuccess { trackRequest ->
                return@async trackRequest.also { logDebug("Cached the track request: $trackRequest") }
            }
            .onFailure { logError("Error while caching the request: $it") }
    }
}
