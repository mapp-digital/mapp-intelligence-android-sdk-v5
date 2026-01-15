package webtrekk.android.sdk.core

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.await
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkInfo
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.WorkerParameters
import androidx.work.await
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import io.mockk.coEvery
import io.mockk.mockkClass
import io.mockk.spyk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.domain.worker.CleanUpWorker
import webtrekk.android.sdk.domain.worker.SendRequestsWorker

class SchedulerImplTest {
    private lateinit var scheduler: Scheduler
    private lateinit var context: Context
    private lateinit var config: Config
    private lateinit var workManager: WorkManager
    private val mutex = Mutex()

    @Before
    fun setUp() {
        val ids = listOf("1234567890")
        val domain = "www.example.com"
        context = InstrumentationRegistry.getInstrumentation().targetContext
        config = spyk(WebtrekkConfiguration.Builder(ids, domain).build())

        val workManagerConfig = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, workManagerConfig)

        workManager = WorkManager.getInstance(context)
        
        // Initialize Webtrekk after WorkManager is set up
        Webtrekk.getInstance().init(context, config)
        
        scheduler = spyk(SchedulerImpl(workManager, config))

        val mockWorker =
            mockkClass(SendRequestsWorker::class, relaxed = true, relaxUnitFun = true)
        coEvery { mockWorker.doWork() } coAnswers {
            mutex.withLock {
                delay(300) // simulate some work
                return@withLock ListenableWorker.Result.success()
            }
        }
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
    }

//    @Test
//    fun schedule_periodicRequest_and_oneTime_request_executes_executes_synchronized() {
//        runBlocking {
//            // Schedule periodic work first
//            scheduler.scheduleSendRequests(1L, Constraints.Builder().build())
//            scheduler.sendRequestsThenCleanUp()
//
//            // Wait for work to execute
//            delay(400)
//
//            // Check that only one SendRequestsWorker finishes (succeeds)
//            val tag = SendRequestsWorker::class.java.name
//            val allWorks = workManager.getWorkInfosByTag(tag).await()
//            val succeededWorks = allWorks.filter { it.state == WorkInfo.State.SUCCEEDED }
//
//            MatcherAssert.assertThat(
//                "Only one work for sending request is finished",
//                succeededWorks.size == 1
//            )
//
//            // Wait for all work to finish before test ends
//            waitForAllWorkToFinish()
//        }
//    }

    @Test
    fun schedule_oneTime_and_periodicRequest_executes_synchronized() {
        runBlocking {
            // Schedule one-time work first, then periodic
            scheduler.sendRequestsThenCleanUp()
            scheduler.scheduleSendRequests(1L, Constraints.Builder().build())
            
            // Wait for work to execute
            delay(400)
            
            // Check that only one SendRequestsWorker finishes (succeeds)
            val tag = SendRequestsWorker::class.java.name
            val allWorks = workManager.getWorkInfosByTag(tag).await()
            val succeededWorks = allWorks.filter { it.state == WorkInfo.State.SUCCEEDED }
            
            MatcherAssert.assertThat(
                "Only one work for sending request are running or it is succeeded",
                succeededWorks.size == 1
            )
            
            // Wait for all work to finish before test ends
            waitForAllWorkToFinish()
        }
    }

    @Test
    fun trigger_only_one_time_requests_for_sending_and_cleaning_data() {
        runBlocking {
            scheduler.sendRequestsThenCleanUp()
            
            // Small delay to allow work to be enqueued
            delay(100)
            
            // Check work by the correct tag (class name, not TAG constant)
            val tag = SendRequestsWorker::class.java.name
            val works = workManager.getWorkInfosByTag(tag).await()
            
            MatcherAssert.assertThat(
                "One time request is triggered",
                works.firstOrNull()?.periodicityInfo == null
            )
            // Wait for all work to finish before test ends
            waitForAllWorkToFinish()
        }
    }

    @Test
    fun trigger_cleanup_worker_as_a_chain_work_of_one_time_request() {
        runBlocking {
            scheduler.sendRequestsThenCleanUp()
            
            // Small delay to allow work to be enqueued
            delay(100)
            
            // Check work by unique work name (the chain contains CleanUpWorker)
            val oneTimeWork = workManager.getWorkInfosForUniqueWork(SchedulerImpl.ONE_TIME_REQUEST).await()
            
            // The chain should contain CleanUpWorker, so check that it's one-time (not periodic)
            MatcherAssert.assertThat(
                "One time request chain is triggered",
                oneTimeWork.isNotEmpty() && oneTimeWork.firstOrNull()?.periodicityInfo == null
            )
            // Wait for all work to finish before test ends
            waitForAllWorkToFinish()
        }
    }

    @Test
    fun trigger_periodic_work_request() {
        runBlocking {
            scheduler.scheduleSendRequests(15, Constraints.Builder().build())
            
            // Small delay to allow work to be enqueued
            delay(100)
            
            // Check periodic work by unique work name
            val periodicWork = workManager.getWorkInfosForUniqueWork(SchedulerImpl.SEND_REQUESTS_WORKER).await()
            
            // Check that it's actually periodic
            val workInfo = periodicWork.firstOrNull()
            MatcherAssert.assertThat(
                "Periodic work request is triggered",
                workInfo?.periodicityInfo != null
            )
            
            // Wait for all work to finish before test ends
            waitForAllWorkToFinish()
        }
    }

    @Test
    fun schedule_and_cancel_all_onetime_work_requests() {
        runBlocking {
            // Schedule one-time work chain (SendRequestsWorker -> CleanUpWorker)
            scheduler.sendRequestsThenCleanUp()
            
            // Small delay to allow work to be enqueued
            delay(100)
            
            // Check work by tags to count individual workers
            val sendTag = SendRequestsWorker::class.java.name
            val cleanTag = CleanUpWorker::class.java.name
            val sendWorks = workManager.getWorkInfosByTag(sendTag).await()
            val cleanWorks = workManager.getWorkInfosByTag(cleanTag).await()
            val allActiveWorks = (sendWorks + cleanWorks).filter { 
                it.state in listOf(WorkInfo.State.RUNNING, WorkInfo.State.SUCCEEDED, WorkInfo.State.ENQUEUED) 
            }
            
            MatcherAssert.assertThat(
                "Work count should be 2 (SendRequestsWorker + CleanUpWorker in chain)",
                allActiveWorks.size == 2
            )
            
            // Cancel by tags
            workManager.cancelAllWorkByTag(sendTag).await()
            workManager.cancelAllWorkByTag(cleanTag).await()
            
            // Wait a bit for cancellation to take effect
            delay(200)
            
            // Verify work is cancelled
            val worksAfterCancel = (workManager.getWorkInfosByTag(sendTag).await() + 
                                   workManager.getWorkInfosByTag(cleanTag).await())
            val stillRunning = worksAfterCancel.filter { 
                it.state == WorkInfo.State.RUNNING 
            }
            
            MatcherAssert.assertThat(
                "Work count should be 0 after cancellation",
                stillRunning.size == 0
            )
            
            // Wait for all work to finish before test ends
            waitForAllWorkToFinish()
        }
    }

    @Test
    fun schedule_and_cancel_all_periodic_work_requests() {
        runBlocking {
            // Schedule periodic work
            scheduler.scheduleSendRequests(15, Constraints.Builder().build())
            
            // Small delay to allow work to be enqueued
            delay(100)
            
            // Check that periodic work exists using the correct tag
            val tag = SendRequestsWorker::class.java.name
            val works = workManager.getWorkInfosByTag(tag).await()
            val activeWorks = works.filter { 
                it.state in listOf(WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED) 
            }
            
            MatcherAssert.assertThat(
                "Work count should be 1 (periodic SendRequestsWorker)",
                activeWorks.size == 1
            )
            
            // Cancel by tag (using the correct tag)
            workManager.cancelAllWorkByTag(tag).await()
            
            // Wait a bit for cancellation to take effect
            delay(200)
            
            // Verify work is cancelled
            val worksAfterCancel = workManager.getWorkInfosByTag(tag).await()
            val stillActive = worksAfterCancel.filter { 
                it.state in listOf(WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED) 
            }
            
            MatcherAssert.assertThat(
                "Work count should be 0 after cancellation",
                stillActive.size == 0
            )
            
            // Wait for all work to finish before test ends
            waitForAllWorkToFinish()
        }
    }

    /**
     * Send different input values for every worker. It needs to do some simple computation. If works are synchronized
     * every work should return 0 value.
     */
    @Test
    fun test_if_multiple_workers_started_are_synchronized() {
        runBlocking {
            val request1 = OneTimeWorkRequest.Builder(TestWorker::class.java)
                .setInputData(Data.Builder().putInt("value", 1).build())
                .build()

            val request2 = OneTimeWorkRequest.Builder(TestWorker::class.java)
                .setInputData(Data.Builder().putInt("value", 2).build())
                .build()

            val request3 = OneTimeWorkRequest.Builder(TestWorker::class.java)
                .setInputData(Data.Builder().putInt("value", 3).build())
                .build()

            val query = WorkQuery.fromIds(
                listOf(
                    request1.id,
                    request2.id,
                    request3.id
                )
            )

            workManager.enqueue(request1)
            workManager.enqueue(request2)
            workManager.enqueue(request3)

            var info = workManager.getWorkInfos(query).await()

            while (info.any { it.state == WorkInfo.State.RUNNING }) {
                delay(100)
                info = workManager.getWorkInfos(query).await()
            }

            println(info.toString())
            val valid = info.filter {
                it.outputData.getInt(
                    "value",
                    0
                ) == 0
            }.size

            MatcherAssert.assertThat("3 Workers are returned 0 value", info.size == valid)
            // Wait for all work to finish before test ends
            waitForAllWorkToFinish()
        }
    }

    @Test
    fun test_synchronization_of_single_method() {
        val counter = Counter()

        val scope = CoroutineScope(Dispatchers.Unconfined)
        val job1 = scope.launch {
            repeat(1000) {
                counter.increment()
            }
        }

        val job2 = scope.launch {
            repeat(1000) {
                counter.increment()
            }
        }

        runBlocking {
            job1.join()
            job2.join()
            val result = counter.getCount()
            println(result)
            MatcherAssert.assertThat("Result should be 2000", 2000 == result)
        }
    }

    private suspend fun waitForAllWorkToFinish() {
        try {
            // Wait for all work (SendRequestsWorker and CleanUpWorker) to finish
            val sendTag = SendRequestsWorker::class.java.name
            val cleanTag = CleanUpWorker::class.java.name
            var sendRequestsInfos = workManager.getWorkInfosByTag(sendTag).await()
            var cleanUpInfos = workManager.getWorkInfosByTag(cleanTag).await()
            var attempts = 0
            while (attempts < 50 && (
                sendRequestsInfos.any { it.state == WorkInfo.State.RUNNING } ||
                cleanUpInfos.any { it.state == WorkInfo.State.RUNNING }
            )) {
                delay(50)
                sendRequestsInfos = workManager.getWorkInfosByTag(sendTag).await()
                cleanUpInfos = workManager.getWorkInfosByTag(cleanTag).await()
                attempts++
            }
            // Additional delay to ensure all database operations from workers are complete
            delay(300)
        } catch (e: Exception) {
            // If there's an error accessing work info, wait a bit to ensure cleanup
            delay(300)
        }
    }
}


class TestWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    var value = 0
    override suspend fun doWork(): Result = coroutineScope {
        mutex.withLock {
            val inc = inputData.getInt("value", 1)
            value += inc
            delay(300)
            value -= inc
            val outputData = Data.Builder().put("value", value).build()
            return@coroutineScope Result.success(outputData)
        }
    }

    companion object {
        val TAG = TestWorker::class.java.simpleName
        val mutex = Mutex()
    }
}

class Counter {
    private var count = 0

    suspend fun increment() {
        mutex.withLock {
            count++
        }
    }

    fun getCount(): Int {
        return count
    }

    companion object{
        val mutex= Mutex()
    }
}