package webtrekk.android.sdk.worker

import androidx.work.*
import webtrekk.android.sdk.Config
import java.util.concurrent.TimeUnit

internal class ScheduleManager {

    fun startScheduling(config: Config) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val worker = PeriodicWorkRequest.Builder(
            DataWorker::class.java,
            config.sendDelay,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance()
            .enqueueUniquePeriodicWork("data worker", ExistingPeriodicWorkPolicy.KEEP, worker)
    }
}
