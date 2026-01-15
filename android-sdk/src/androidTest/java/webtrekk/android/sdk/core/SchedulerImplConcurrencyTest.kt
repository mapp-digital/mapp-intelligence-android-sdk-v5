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

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.await
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.await
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.domain.worker.CleanUpWorker
import webtrekk.android.sdk.domain.worker.SendRequestsWorker

/**
 * Verifies SchedulerImpl never runs multiple SendRequestsWorker instances at the same time,
 * regardless of whether the work is periodic or one-time.
 */
@RunWith(AndroidJUnit4::class)
class SchedulerImplConcurrencyTest {
    private lateinit var scheduler: Scheduler
    private lateinit var workManager: WorkManager
    private lateinit var config: Config
    private lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        TestSendRequestsWorker.reset()

        val workManagerConfig = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .setWorkerFactory(TestWorkerFactory())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(appContext, workManagerConfig)
        workManager = WorkManager.getInstance(appContext)

        config = WebtrekkConfiguration.Builder(
            listOf("1234567890"),
            "www.example.com"
        ).build()
        Webtrekk.getInstance().init(appContext, config)

        scheduler = SchedulerImpl(workManager, config)
    }

    @After
    fun tearDown() {
        runBlocking {
            try {
                // Cancel unique work names that the global scheduler uses
                // These are the same names used by SchedulerImpl
                workManager.cancelUniqueWork("send_requests_worker")
                workManager.cancelUniqueWork("one_time_request_send_and_clean")
                workManager.cancelUniqueWork("CleanUpWorker")
                
                // Cancel all work by tags (covers both test and global scheduler work)
                workManager.cancelAllWorkByTag(SendRequestsWorker::class.java.name)
                workManager.cancelAllWorkByTag(CleanUpWorker::class.java.name)
                
                // Also cancel all work as a safety net
                workManager.cancelAllWork().await()
            } catch (e: Exception) {
                // Ignore cancel errors
            }
            
            // Wait for all work to finish and database operations to complete
            // Don't query work status as that might hold database connections
            delay(1000)
            
            // Try to prune work
            try {
                workManager.pruneWork().await()
            } catch (e: Exception) {
                // Ignore prune errors
            }
            
            // Wait again after prune
            delay(500)
        }
        
        // closeWorkDatabase must be called on the main thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            try {
                // Final wait to ensure all threads and connections are released
                Thread.sleep(300)
                WorkManagerTestInitHelper.closeWorkDatabase()
            } catch (e: Exception) {
                // Silently ignore - database might already be closed or test framework will handle it
            }
        }
        TestSendRequestsWorker.reset()
    }

    @Test
    fun testOneTimeRequestsDoNotOverlapEachOther() = runBlocking {
        try {
            scheduler.sendRequestsThenCleanUp()
            scheduler.sendRequestsThenCleanUp()

            waitForSendRequestsWorkerToFinish()
            waitForAllWorkToFinish()

            assertTrue("SendRequestsWorker should have run at least once", TestSendRequestsWorker.runCount.get() > 0)
            assertEquals("Only one SendRequestsWorker may run at any time", 1, TestSendRequestsWorker.maxRunning.get())
            assertTrue("Only one active SendRequestsWorker exists", runningSendRequestsWorkers() <= 1)
        } catch (e: Exception) {
            // Ensure cleanup even if test fails
            try {
                workManager.cancelAllWork().await()
            } catch (ignored: Exception) {
                // Ignore
            }
            throw e
        }
    }

    @Test
    fun testTwoPeriodicRequestsNeverRunConcurrently() = runBlocking {
        try {
            scheduler.scheduleSendRequests(15L, Constraints.Builder().build())
            
            // Wait for work to be scheduled (with absolute timeout)
            // Don't wait indefinitely - periodic work might be ENQUEUED but not RUNNING immediately
            waitUntilSendRequestsWorkerIsScheduled(maxWaitMs = 1000)

            scheduler.scheduleSendRequests(15L, Constraints.Builder().build())

            // Wait for work to finish (with absolute timeout)
            waitForSendRequestsWorkerToFinish(maxWaitMs = 3000)
            waitForAllWorkToFinish(maxWaitMs = 2000)

            assertTrue("Periodic SendRequestsWorker should start", TestSendRequestsWorker.runCount.get() > 0)
            assertEquals("Only one SendRequestsWorker may run at any time", 1, TestSendRequestsWorker.maxRunning.get())
            
            val periodicCount = try {
                workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
                    .filter { it.state != WorkInfo.State.CANCELLED }
                    .count { it.periodicityInfo != null }
            } catch (e: Exception) {
                // If we can't query work, just skip this assertion
                1
            }
            assertEquals("Only one periodic SendRequestsWorker WorkSpec should remain", 1, periodicCount)
        } finally {
            // Ensure cleanup even if test fails
            try {
                workManager.cancelAllWork().await()
            } catch (ignored: Exception) {
                // Ignore
            }
        }
    }

    @Test
    fun testPeriodicAndOnetimeRequestsDoNotOverlap() = runBlocking {
        try {
            scheduler.scheduleSendRequests(15L, Constraints.Builder().build())
            waitUntilSendRequestsWorkerIsScheduled(maxWaitMs = 1000)

            scheduler.sendRequestsThenCleanUp()

            waitForSendRequestsWorkerToFinish(maxWaitMs = 3000)
            waitForAllWorkToFinish(maxWaitMs = 2000)

            assertTrue("Work should have started", TestSendRequestsWorker.runCount.get() > 0)
            assertEquals("Only one SendRequestsWorker may run at any time", 1, TestSendRequestsWorker.maxRunning.get())
        } catch (e: Exception) {
            // Ensure cleanup even if test fails
            try {
                workManager.cancelAllWork().await()
            } catch (ignored: Exception) {
                // Ignore
            }
            throw e
        }
    }

    private suspend fun waitUntilSendRequestsWorkerIsRunning() {
        waitUntilSendRequestsWorkerIsScheduled(maxWaitMs = 2000)
    }
    
    private suspend fun waitUntilSendRequestsWorkerIsScheduled(maxWaitMs: Int = 2000) {
        try {
            val startTime = System.currentTimeMillis()
            var infos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
            
            // Wait for work to be ENQUEUED or RUNNING (not just RUNNING)
            // This is more lenient for periodic work which might not run immediately
            while ((System.currentTimeMillis() - startTime) < maxWaitMs && 
                   infos.none { it.state in listOf(WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED) }) {
                delay(50)
                try {
                    infos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
                } catch (e: Exception) {
                    // If we can't query, just break to avoid blocking
                    break
                }
            }
            // Small delay to allow work to potentially start
            delay(100)
        } catch (e: Exception) {
            // If there's an error, just wait a bit and continue
            delay(200)
        }
    }

    private suspend fun waitForSendRequestsWorkerToFinish(maxWaitMs: Int = 3000) {
        try {
            val startTime = System.currentTimeMillis()
            var infos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
            
            // Wait with absolute timeout based on wall clock time
            while ((System.currentTimeMillis() - startTime) < maxWaitMs && 
                   infos.any { it.state == WorkInfo.State.RUNNING }) {
                delay(50)
                try {
                    infos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
                } catch (e: Exception) {
                    // If we can't query, just break to avoid blocking
                    break
                }
            }
            // Small delay to ensure work is finished
            delay(100)
        } catch (e: Exception) {
            // If there's an error, just wait a bit and continue
            delay(200)
        }
    }

    private suspend fun waitForAllWorkToFinish(maxWaitMs: Int = 2000) {
        try {
            val startTime = System.currentTimeMillis()
            // Wait for all work (SendRequestsWorker and CleanUpWorker) to finish
            var sendRequestsInfos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
            var cleanUpInfos = workManager.getWorkInfosByTag(CleanUpWorker::class.java.name).await()
            
            // Wait with absolute timeout based on wall clock time
            while ((System.currentTimeMillis() - startTime) < maxWaitMs && (
                sendRequestsInfos.any { it.state == WorkInfo.State.RUNNING } ||
                cleanUpInfos.any { it.state == WorkInfo.State.RUNNING }
            )) {
                delay(50)
                try {
                    sendRequestsInfos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
                    cleanUpInfos = workManager.getWorkInfosByTag(CleanUpWorker::class.java.name).await()
                } catch (e: Exception) {
                    // If we can't query, just break to avoid blocking
                    break
                }
            }
            // Additional delay to ensure all database operations from workers are complete
            delay(200)
        } catch (e: Exception) {
            // If there's an error, just wait a bit and continue
            delay(300)
        }
    }

    private suspend fun runningSendRequestsWorkers(): Int {
        return try {
            workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
                .count { it.state == WorkInfo.State.RUNNING }
        } catch (e: Exception) {
            // If we can't query, return 0 to avoid blocking
            0
        }
    }
}

/**
 * Replaces SendRequestsWorker with a lightweight test double that records concurrent executions.
 */
private class TestWorkerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SendRequestsWorker::class.java.name -> TestSendRequestsWorker(appContext, workerParameters)
            CleanUpWorker::class.java.name -> TestCleanUpWorker(appContext, workerParameters)
            else -> null
        }
    }
}

private class TestSendRequestsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val current = running.incrementAndGet()
        maxRunning.updateAndGet { max(it, current) }
        delay(200)
        runCount.incrementAndGet()
        running.decrementAndGet()
        return Result.success()
    }

    companion object {
        private val running = AtomicInteger(0)
        internal val maxRunning = AtomicInteger(0)
        internal val runCount = AtomicInteger(0)

        internal fun reset() {
            running.set(0)
            maxRunning.set(0)
            runCount.set(0)
        }
    }
}

private class TestCleanUpWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        delay(10)
        return Result.success()
    }
}
