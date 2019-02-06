package webtrekk.android.sdk.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import webtrekk.android.sdk.data.DaoProvider
import webtrekk.android.sdk.data.repository.TrackRequestRepositoryImpl
import webtrekk.android.sdk.domain.internal.GetCachedTrackRequests
import webtrekk.android.sdk.logDebug

internal class DataTrackWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {

    // todo implement by injection
    private val trackRequestRepository =
        TrackRequestRepositoryImpl(DaoProvider.provideTrackRequestDao(context))
    private val getCachedTrackRequests =
        GetCachedTrackRequests(trackRequestRepository, coroutineContext)

    override suspend fun doWork(): Result {
        val result = getCachedTrackRequests().await()
        logDebug("got data in work manager $result")

        return Result.success()
    }
}
