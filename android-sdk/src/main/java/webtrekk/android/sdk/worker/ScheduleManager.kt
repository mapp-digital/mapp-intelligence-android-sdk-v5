package webtrekk.android.sdk.worker

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.impl.WorkManagerImpl
import webtrekk.android.sdk.Config
import java.util.concurrent.TimeUnit

internal class ScheduleManager {

    fun startScheduling(config: Config) {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
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
