package webtrekk.android.sdk.domain.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import kotlin.coroutines.CoroutineContext

internal class GetCachedTrackRequests(
    private val trackRequestRepository: TrackRequestRepository,
    coroutineContext: CoroutineContext
) {

    private val scope = CoroutineScope(coroutineContext + Dispatchers.IO)

    operator fun invoke() = scope.async {
        val result = trackRequestRepository.getTrackRequests()

        when (result) {
            is DataResult.Success -> return@async result.data.also { logDebug("Got cached track requests: ${result.data}") }
            is DataResult.Fail -> logError("Error while getting cached track requests: ${result.exception}")
        }
    }
}
