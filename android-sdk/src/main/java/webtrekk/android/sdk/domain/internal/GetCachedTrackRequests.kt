package webtrekk.android.sdk.domain.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
        trackRequestRepository.getTrackRequests()
            .onSuccess { dataTracks ->
                return@async dataTracks.also { logDebug("Got cached track requests: $dataTracks") }
            }
            .onFailure { logError("Error while getting cached track requests: $it") }
    }
}
