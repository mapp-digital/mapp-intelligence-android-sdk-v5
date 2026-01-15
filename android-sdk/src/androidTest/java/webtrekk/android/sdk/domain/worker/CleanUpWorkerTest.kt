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

package webtrekk.android.sdk.domain.worker

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class CleanUpWorkerTest : WorkManagerTest() {

    @Test
    @Throws(Exception::class)
    fun testCleanUpWorker_OneTimeWork() {
        val cleanUpWorker = OneTimeWorkRequest.Builder(CleanUpWorker::class.java)
            .build()

        val workManager = WorkManager.getInstance(context)

        // Enqueue the work
        workManager.enqueue(cleanUpWorker).result.get()

        // Small delay to allow work to be enqueued
        Thread.sleep(100)

        // Check work state
        val workInfo = workManager.getWorkInfoById(cleanUpWorker.id).get()

        // Work should be in one of these states: ENQUEUED (waiting to run), RUNNING (executing), or SUCCEEDED (already finished)
        assertThat(
            "Work should be enqueued, running, or succeeded",
            workInfo?.state,
            anyOf(
                `is`(WorkInfo.State.RUNNING), 
                `is`(WorkInfo.State.ENQUEUED),
                `is`(WorkInfo.State.SUCCEEDED)
            )
        )
    }
}
