package webtrekk.android.sdk.domain.internal

import android.util.Log
import kotlinx.coroutines.*
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.model.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import kotlin.coroutines.CoroutineContext

internal class AddTrackRequest(
    private val trackRequestRepository: TrackRequestRepository,
    coroutineContext: CoroutineContext
) {

    var scope = CoroutineScope(coroutineContext + Dispatchers.IO)

    operator fun invoke(trackRequest: TrackRequest) = scope.async {
        val result = trackRequestRepository.addTrackRequest(trackRequest)

        when (result) {
            is DataResult.Success
            -> {
                Log.wtf("Activity", "Added ${result.data} to the database")
                return@async (result.data)
            }
            is DataResult.Fail -> Log.wtf("Activity", result.exception)
        }
    }
}
