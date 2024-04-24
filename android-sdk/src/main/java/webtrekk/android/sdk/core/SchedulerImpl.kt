/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package webtrekk.android.sdk.core

import android.os.Build
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.domain.worker.CleanUpWorker
import webtrekk.android.sdk.domain.worker.SendRequestsWorker
import webtrekk.android.sdk.util.webtrekkLogger
import java.util.concurrent.TimeUnit

/**
 * The implementation of [Scheduler] using [WorkManager].
 */
internal class SchedulerImpl(private val workManager: WorkManager, private val config: Config) :
    Scheduler {

    override fun scheduleSendRequests(
        repeatInterval: Long,
        constraints: Constraints,
    ) {
        val data = Data.Builder().apply {
            putStringArray("trackIds", config.trackIds.toTypedArray())
            putString("trackDomain", config.trackDomain)
        }.build()

        val workBuilder = PeriodicWorkRequest.Builder(
            SendRequestsWorker::class.java,
            repeatInterval,
            TimeUnit.MINUTES
        ).setConstraints(constraints)
            .setInitialDelay(0, TimeUnit.SECONDS)
            .setInputData(data)
            .addTag(SendRequestsWorker.TAG)

        val sendRequestsWorker = workBuilder.build()

        workManager.enqueueUniquePeriodicWork(
            SEND_REQUESTS_WORKER,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            sendRequestsWorker
        )
    }

    override fun sendRequestsThenCleanUp() {
        // check if SendRequestsWorker already running as periodic work request
        val workers = workManager.getWorkInfosByTag(SendRequestsWorker.TAG).get()

        if (workers.isEmpty() || workers[0].state != WorkInfo.State.RUNNING) {
            scheduleSendAndCleanWorkers()
        }
    }

    private fun scheduleSendAndCleanWorkers() {
        val data = Data.Builder().apply {
            putStringArray("trackIds", config.trackIds.toTypedArray())
            putString("trackDomain", config.trackDomain)
        }.build()

        val sendWorkBuilder = OneTimeWorkRequest.Builder(SendRequestsWorker::class.java)
            .addTag(SendRequestsWorker.TAG_ONE_TIME_WORKER)
            .setInputData(data)

        val cleanWorkBuilder = OneTimeWorkRequest.Builder(CleanUpWorker::class.java)
            .setInputData(data)
            .addTag(CleanUpWorker.TAG)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sendWorkBuilder.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            cleanWorkBuilder.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        }

        workManager.enqueueUniqueWork(
            "oneTimeRequest",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            listOf(sendWorkBuilder.build(), cleanWorkBuilder.build())
        )
    }

    // To be changed to clean up after executing the requests
    override fun scheduleCleanUp() {
        val data = Data.Builder().apply {
            putStringArray("trackIds", config.trackIds.toTypedArray())
            putString("trackDomain", config.trackDomain)
        }.build()

        val cleanWorkBuilder = OneTimeWorkRequest.Builder(CleanUpWorker::class.java)
            .addTag(CleanUpWorker.TAG)
            .setInputData(data)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            cleanWorkBuilder.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        }

        workManager.enqueueUniqueWork(
            "CleanUpWorker",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            cleanWorkBuilder.build()
        )
    }

    override fun cancelScheduleSendRequests() {
        workManager.cancelAllWorkByTag(CleanUpWorker.TAG)
        workManager.cancelAllWorkByTag(SendRequestsWorker.TAG)
    }

    companion object {
        const val SEND_REQUESTS_WORKER = "send_requests_worker"
    }
}
