package webtrekk.android.sdk.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import webtrekk.android.sdk.data.DaoProvider
import webtrekk.android.sdk.data.repository.TrackRequestRepositoryImpl
import webtrekk.android.sdk.domain.internal.GetCachedTrackRequests

internal class DataTrackWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {

    private val trackRequestRepository =
        TrackRequestRepositoryImpl(DaoProvider.provideTrackRequestDao(context))
    private val getCachedTrackRequests =
        GetCachedTrackRequests(trackRequestRepository, coroutineContext)

    override suspend fun doWork(): Result {
        val result = getCachedTrackRequests().await()

        return Result.success()
    }
}
