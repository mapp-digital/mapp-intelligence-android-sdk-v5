package webtrekk.android.sdk.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.internal.ClearTrackRequests
import webtrekk.android.sdk.domain.internal.GetCachedDataTracks
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import webtrekk.android.sdk.logInfo
import webtrekk.android.sdk.util.CoroutineDispatchers

internal class CleanUpWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    private val coroutineDispatchers: CoroutineDispatchers by inject()
    private val getCachedDataTracks: GetCachedDataTracks by inject()
    private val clearTrackRequests: ClearTrackRequests by inject()

    override val coroutineContext: CoroutineDispatcher
        get() = coroutineDispatchers.ioDispatcher

    override suspend fun doWork(): Result {
        getCachedDataTracks(GetCachedDataTracks.Params(requestStates = listOf(TrackRequest.RequestState.DONE)))
            .onSuccess { dataTracks ->
                if (dataTracks.isNotEmpty()) {
                    logInfo("Cleaning up the completed requests")

                    clearTrackRequests(ClearTrackRequests.Params(trackRequests = dataTracks.map { it.trackRequest }))
                        .onSuccess { logDebug("Cleaned up the completed requests successfully") }
                        .onFailure {
                            logError("Failed while cleaning up the completed requests: $it")

                            return Result.failure()
                        }
                }
            }
            .onFailure { logError("Error getting the cached completed requests: $it") }

        return Result.success()
    }

    companion object {
        const val TAG = "clean_up"
    }
}
