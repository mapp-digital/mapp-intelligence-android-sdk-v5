package webtrekk.android.sdk.domain.internal

import kotlinx.coroutines.*
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.model.TrackRequest
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
        val result = trackRequestRepository.addTrackRequest(trackRequest)

        when (result) {
            is DataResult.Success -> return@async result.data.also { logDebug("Cached the track request: ${result.data}") }
            is DataResult.Fail -> logError("Error while caching the request: ${result.exception}")
        }
    }
}
