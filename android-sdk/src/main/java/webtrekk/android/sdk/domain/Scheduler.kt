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

package webtrekk.android.sdk.domain

import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequest
import androidx.work.OneTimeWorkRequest
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
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

    fun sendRequestsThenCleanUp() {
        val sendRequestsWorker = OneTimeWorkRequest.Builder(SendRequestsWorker::class.java)
            .addTag(SendRequestsWorker.TAG_ONE_TIME_WORKER)
            .build()

        val cleanUpWorker = OneTimeWorkRequest.Builder(CleanUpWorker::class.java)
            .addTag(CleanUpWorker.TAG)
            .build()

        workManager.beginWith(sendRequestsWorker)
            .then(cleanUpWorker)
            .enqueue()
    }

    // To be changed to clean up upon executing the requests
    fun scheduleCleanUp() {
        val cleanUpWorker = OneTimeWorkRequest.Builder(CleanUpWorker::class.java)
            .addTag(CleanUpWorker.TAG)
            .build()

        workManager.enqueue(cleanUpWorker)
    }

    fun cancelScheduleSendRequests() {
        workManager.cancelAllWorkByTag(SendRequestsWorker.TAG)
    }

    companion object {
        const val SEND_REQUESTS_WORKER = "send_requests_worker"
    }
}
