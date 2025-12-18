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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.domain.worker.CleanUpWorker
import webtrekk.android.sdk.domain.worker.SendRequestsWorker
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.util.webtrekkLogger
import java.util.concurrent.TimeUnit

/**
 * The implementation of [Scheduler] using [WorkManager].
 */
internal class SchedulerImpl(
    private val workManager: WorkManager,
    private val config: Config,
) :
    Scheduler {
    override suspend fun scheduleSendRequests(
        repeatInterval: Long,
        constraints: Constraints,
    ) {
        withContext(AppModule.dispatchers.mainDispatcher) {
            webtrekkLogger.debug("SEND WORKER - scheduleSendRequests")
            mutex.withLock {
                val data = Data.Builder().apply {
                    putStringArray("trackIds", config.trackIds.toTypedArray())
                    putString("trackDomain", config.trackDomain)
                }.build()

                val workBuilder = PeriodicWorkRequestBuilder<SendRequestsWorker>(
                    repeatInterval,
                    TimeUnit.MINUTES
                ).setConstraints(constraints)
                    .setInitialDelay(0, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag(SendRequestsWorker::class.java.name)

                val sendRequestsWorker = workBuilder.build()

                workManager.enqueueUniquePeriodicWork(
                    SEND_REQUESTS_WORKER,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    sendRequestsWorker
                )
            }
        }
    }

    // To be changed to clean up after executing the requests
    override suspend fun scheduleCleanUp() {
        withContext(AppModule.dispatchers.mainDispatcher) {
            mutex.withLock {
                val data = Data.Builder().apply {
                    putStringArray("trackIds", config.trackIds.toTypedArray())
                    putString("trackDomain", config.trackDomain)
                }.build()

                val cleanWorkBuilder =
                    PeriodicWorkRequestBuilder<CleanUpWorker>(
                        60,
                        TimeUnit.MINUTES
                    ).setInitialDelay(5, TimeUnit.MINUTES)
                        .setInputData(data)

                workManager.enqueueUniquePeriodicWork(
                    CLEAN_UP_WORKER,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    cleanWorkBuilder.build()
                )
            }
        }
    }

    override suspend fun sendRequestsThenCleanUp() {
        withContext(AppModule.dispatchers.mainDispatcher) {
            webtrekkLogger.debug("SEND WORKER - sendRequestsThenCleanUp")
            mutex.withLock {
                val activeStates = listOf(
                    WorkInfo.State.RUNNING,
                    WorkInfo.State.BLOCKED,
                )

                // check if SendRequestsWorker already running/enqueued as periodic or one-time work
                val periodicQuery = WorkQuery.Builder
                    .fromTags(listOf(SendRequestsWorker::class.java.name))
                    .addStates(activeStates)
                    .build()
                val periodicWorkers = workManager.getWorkInfos(periodicQuery).get()

                val oneTimeQuery = WorkQuery.Builder
                    .fromUniqueWorkNames(listOf(ONE_TIME_REQUEST))
                    .addStates(activeStates)
                    .build()
                val oneTimeWorkers = workManager.getWorkInfos(oneTimeQuery).get()

                val hasActiveSend = periodicWorkers.any { it.state in activeStates } ||
                        oneTimeWorkers.any { it.state in activeStates }

                if (!hasActiveSend) {
                    scheduleSendAndCleanWorkers()
                }
            }
        }
    }

    private fun scheduleSendAndCleanWorkers() {
        val data = Data.Builder().apply {
            putStringArray("trackIds", config.trackIds.toTypedArray())
            putString("trackDomain", config.trackDomain)
        }.build()

        val sendWorkBuilder = OneTimeWorkRequest.Builder(SendRequestsWorker::class.java)
            .setInputData(data)
            .addTag(SendRequestsWorker::class.java.name)

        val cleanWorkBuilder = OneTimeWorkRequest.Builder(CleanUpWorker::class.java)
            .setInputData(data)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sendWorkBuilder.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            cleanWorkBuilder.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        }

        workManager
            .beginUniqueWork(ONE_TIME_REQUEST, ExistingWorkPolicy.REPLACE, sendWorkBuilder.build())
            .then(cleanWorkBuilder.build())
            .enqueue()
    }

    override suspend fun cancelScheduleSendRequests() {
        workManager.cancelUniqueWork(CLEAN_UP_WORKER)
        workManager.cancelUniqueWork(ONE_TIME_REQUEST)
        workManager.cancelUniqueWork(SEND_REQUESTS_WORKER)
    }

    override suspend fun pruneWorks() {
        workManager.pruneWork().await()
    }

    companion object {
        private val mutex = Mutex()
        const val SEND_REQUESTS_WORKER = "send_requests_worker"
        const val ONE_TIME_REQUEST = "one_time_request_send_and_clean"
        const val CLEAN_UP_WORKER = "CleanUpWorker"
    }
}
