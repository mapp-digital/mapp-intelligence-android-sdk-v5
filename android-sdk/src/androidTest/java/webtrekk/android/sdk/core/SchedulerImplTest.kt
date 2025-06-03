package webtrekk.android.sdk.core

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.await
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.WorkerParameters
import androidx.work.await
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import io.mockk.coEvery
import io.mockk.mockkClass
import io.mockk.spyk
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

        Webtrekk.getInstance().init(context, config)

        val workManagerConfig = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, workManagerConfig)

        workManager = WorkManager.getInstance(context)
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
            workManager.cancelAllWork().await()
            workManager.pruneWork().await()
            WorkManagerTestInitHelper.closeWorkDatabase()
        }
    }

    @Test
    fun schedule_periodicRequest_and_oneTime_request_executes_executes_synchronized() {
        runBlocking {
            // we start both works at the same time
            scheduler.scheduleSendRequests(1L, Constraints.Builder().build())
            scheduler.sendRequestsThenCleanUp()
            //wait 400 milliseconds and check result
            delay(400)
            val works = workManager.getWorkInfosByTag(SendRequestsWorker.TAG).await()
            MatcherAssert.assertThat(
                "Only one work for sending request is finished",
                works.filter {
                    it.state in listOf(
                        WorkInfo.State.SUCCEEDED
                    )
                }.size == 1
            )
        }
    }

    @Test
    fun schedule_oneTime_and_periodicRequest_executes_synchronized() {
        runBlocking {
            // we start both works at the same time
            scheduler.sendRequestsThenCleanUp()
            scheduler.scheduleSendRequests(1L, Constraints.Builder().build())
            //wait 400 milliseconds and check result
            delay(400)
            val works = workManager.getWorkInfosByTag(SendRequestsWorker.TAG).await()
            println(works.toString())
            MatcherAssert.assertThat(
                "Only one work for sending request are running or it is succeeded",
                works.filter {
                    it.state in listOf(
                        WorkInfo.State.SUCCEEDED,
                    )
                }.size == 1
            )
        }
    }

    @Test
    fun trigger_only_one_time_requests_for_sending_and_cleaning_data() {
        runBlocking {
            scheduler.sendRequestsThenCleanUp()
            val works = workManager.getWorkInfosByTag(SendRequestsWorker.TAG).await()
            MatcherAssert.assertThat(
                "One time request is triggered",
                works.firstOrNull()?.periodicityInfo == null
            )
        }
    }

    @Test
    fun trigger_cleanup_worker_as_a_chain_work_of_one_time_request() {
        runBlocking {
            scheduler.sendRequestsThenCleanUp()
            val works = workManager.getWorkInfosByTag(CleanUpWorker.TAG).await()
            MatcherAssert.assertThat(
                "One time request is triggered",
                works.firstOrNull()?.periodicityInfo == null
            )
        }
    }

    @Test
    fun trigger_periodic_work_request() {
        runBlocking {
            scheduler.scheduleSendRequests(15, Constraints.Builder().build())
            val works = workManager.getWorkInfosByTag(SendRequestsWorker.TAG).await()
            MatcherAssert.assertThat(
                "Periodic work request is triggered",
                works.firstOrNull()?.periodicityInfo != null
            )
        }
    }

    @Test
    fun schedule_and_cancel_all_onetime_work_requests() {
        runBlocking {
            val tags = listOf(
                SendRequestsWorker.TAG,
                CleanUpWorker.TAG
            )
            val validWorkStates =
                listOf(WorkInfo.State.RUNNING, WorkInfo.State.SUCCEEDED, WorkInfo.State.ENQUEUED)
            val workQuery = WorkQuery.fromTags(tags)
            var workCount: Int = 0
            scheduler.sendRequestsThenCleanUp()
            workCount =
                workManager.getWorkInfos(workQuery).await()
                    .filter { it.state in validWorkStates }.size

            MatcherAssert.assertThat("Work count should be 2", workCount == 2)
            for (tag in tags) {
                workManager.cancelAllWorkByTag(tag).await()
            }

            workCount = workManager.getWorkInfos(workQuery).await()
                .filter { it.state == WorkInfo.State.RUNNING }.size
            MatcherAssert.assertThat("Work count should be 0", workCount == 0)
        }
    }

    @Test
    fun schedule_and_cancel_all_periodic_work_requests() {
        runBlocking {
            val tags = listOf(
                SendRequestsWorker.TAG,
                CleanUpWorker.TAG
            )
            val validWorkStates = listOf(WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED)
            val workQuery = WorkQuery.fromTags(tags)
            var workCount: Int = 0
            scheduler.scheduleSendRequests(15, Constraints.Builder().build())
            workCount =
                workManager.getWorkInfos(workQuery).get()
                    .filter { it.state in validWorkStates }.size

            MatcherAssert.assertThat("Work count should be 1", workCount == 1)
            tags.forEach { workManager.cancelAllWorkByTag(it) }

            workCount =
                workManager.getWorkInfos(workQuery).get()
                    .filter { it.state in validWorkStates }.size
            MatcherAssert.assertThat("Work count should be 0", workCount == 0)
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
        }
    }

    @Test
    fun test_synchronization_of_single_method() {
        val counter = Counter()

        val job1 = GlobalScope.launch {
            repeat(1000) {
                counter.increment()
            }
        }

        val job2 = GlobalScope.launch {
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