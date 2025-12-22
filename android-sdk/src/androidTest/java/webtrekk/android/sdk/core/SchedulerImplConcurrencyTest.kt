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
            workManager.cancelAllWork().await()
            workManager.pruneWork().await()
        }
        WorkManagerTestInitHelper.closeWorkDatabase()
        TestSendRequestsWorker.reset()
    }

    @Test
    fun testOneTimeRequestsDoNotOverlapEachOther() = runBlocking {
        scheduler.sendRequestsThenCleanUp()
        scheduler.sendRequestsThenCleanUp()

        waitForSendRequestsWorkerToFinish()

        assertTrue("SendRequestsWorker should have run at least once", TestSendRequestsWorker.runCount.get() > 0)
        assertEquals("Only one SendRequestsWorker may run at any time", 1, TestSendRequestsWorker.maxRunning.get())
        assertTrue("Only one active SendRequestsWorker exists", runningSendRequestsWorkers() <= 1)
    }

    @Test
    fun testTwoPeriodicRequestsNeverRunConcurrently() = runBlocking {
        scheduler.scheduleSendRequests(15L, Constraints.Builder().build())
        waitUntilSendRequestsWorkerIsRunning()

        scheduler.scheduleSendRequests(15L, Constraints.Builder().build())

        waitForSendRequestsWorkerToFinish()

        assertTrue("Periodic SendRequestsWorker should start", TestSendRequestsWorker.runCount.get() > 0)
        assertEquals("Only one SendRequestsWorker may run at any time", 1, TestSendRequestsWorker.maxRunning.get())
        val periodicCount = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
            .filter { it.state != WorkInfo.State.CANCELLED }
            .count { it.periodicityInfo != null }
        assertEquals("Only one periodic SendRequestsWorker WorkSpec should remain", 1, periodicCount)
    }

    @Test
    fun testPeriodicAndOnetimeRequestsDoNotOverlap() = runBlocking {
        scheduler.scheduleSendRequests(15L, Constraints.Builder().build())
        waitUntilSendRequestsWorkerIsRunning()

        scheduler.sendRequestsThenCleanUp()

        waitForSendRequestsWorkerToFinish()

        assertTrue("Work should have started", TestSendRequestsWorker.runCount.get() > 0)
        assertEquals("Only one SendRequestsWorker may run at any time", 1, TestSendRequestsWorker.maxRunning.get())
    }

    private suspend fun waitUntilSendRequestsWorkerIsRunning() {
        var infos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
        var attempts = 0
        while (infos.none { it.state == WorkInfo.State.RUNNING } && attempts < 80) {
            delay(25)
            attempts++
            infos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
        }
    }

    private suspend fun waitForSendRequestsWorkerToFinish() {
        var infos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
        var attempts = 0
        while (infos.any { it.state == WorkInfo.State.RUNNING } && attempts < 80) {
            delay(25)
            attempts++
            infos = workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
        }
    }

    private suspend fun runningSendRequestsWorkers(): Int =
        workManager.getWorkInfosByTag(SendRequestsWorker::class.java.name).await()
            .count { it.state == WorkInfo.State.RUNNING }
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
