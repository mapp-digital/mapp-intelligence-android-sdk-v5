package webtrekk.android.sdk.domain.internal

import androidx.work.*
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.worker.DataTrackWorker
import java.util.concurrent.TimeUnit

private const val DATA_TRACKS_WORKER = "data_track_worker"

internal class Scheduler(
    trackRequestRepository: TrackRequestRepository
) {

    operator fun invoke(sendDelay: Long, constraints: Constraints) {
        val worker = PeriodicWorkRequest.Builder(
            DataTrackWorker::class.java,
            sendDelay,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .build()

        val result = WorkManager.getInstance()
            .enqueueUniquePeriodicWork(
                DATA_TRACKS_WORKER,
                ExistingPeriodicWorkPolicy.KEEP,
                worker
            )
    }
}
