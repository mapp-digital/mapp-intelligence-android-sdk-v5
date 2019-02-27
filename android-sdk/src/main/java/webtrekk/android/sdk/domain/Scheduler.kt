package webtrekk.android.sdk.domain

import androidx.work.*
import webtrekk.android.sdk.domain.worker.SendRequestsWorker
import webtrekk.android.sdk.domain.worker.CleanUpWorker
import java.util.concurrent.TimeUnit

internal class Scheduler(private val workManager: WorkManager) {

    fun scheduleSendRequests(sendDelay: Long, constraints: Constraints) {
        val sendRequestWorker = PeriodicWorkRequest.Builder(
            SendRequestsWorker::class.java,
            sendDelay,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .addTag(SendRequestsWorker.TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SEND_REQUESTS_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            sendRequestWorker
        )
    }

    // To be changed to clean up upon executing the requests
    fun scheduleCleanUp() {
        val cleanUpWorker = OneTimeWorkRequest.Builder(CleanUpWorker::class.java)
            .addTag(CleanUpWorker.TAG)
            .build()

        workManager.enqueue(cleanUpWorker)
    }

    fun cancelSendRequests() {
        workManager.cancelAllWorkByTag(SendRequestsWorker.TAG)
    }

    companion object {
        const val SEND_REQUESTS_WORKER = "send_requests_worker"
    }
}
