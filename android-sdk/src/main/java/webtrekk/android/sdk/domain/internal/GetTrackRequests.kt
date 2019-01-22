package webtrekk.android.sdk.domain.internal

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import kotlin.coroutines.CoroutineContext

internal class GetTrackRequests(
    private val trackRequestRepository: TrackRequestRepository,
    coroutineContext: CoroutineContext
) {

    var scope = CoroutineScope(coroutineContext + Dispatchers.IO)

    // for testing
    internal lateinit var testResult: DataResult<Any>

    operator fun invoke() = scope.launch {
        val result = trackRequestRepository.getTrackRequests()
        testResult = result

        when (result) {
            is DataResult.Success -> Log.wtf("data", result.data.toString())
            is DataResult.Fail -> Log.wtf("exception", result.exception)
        }
    }
}
