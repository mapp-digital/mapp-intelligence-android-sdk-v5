package webtrekk.android.sdk.core

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.domain.worker.CleanUpWorker
import webtrekk.android.sdk.domain.worker.SendRequestsWorker

class SchedulerImplTest {
    private lateinit var scheduler: Scheduler
    private lateinit var context: Context
    private lateinit var config: Config
    private lateinit var workManager: WorkManager

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
        scheduler = spyk(SchedulerImpl(workManager, config))
    }

    @After
    fun tearDown() {
        workManager.cancelAllWork()
        workManager.pruneWork()
        WorkManagerTestInitHelper.closeWorkDatabase()
    }

    @Test
    fun schedule_Periodic_and_Onetime_request_executes_only_periodic() {
        scheduler.scheduleSendRequests(1L, Constraints.Builder().build())
        scheduler.sendRequestsThenCleanUp()
        val works = workManager.getWorkInfosByTag(SendRequestsWorker.TAG).get()
        MatcherAssert.assertThat(
            "Only one work for sending request are running",
            works.filter { it.state in listOf(WorkInfo.State.RUNNING, WorkInfo.State.SUCCEEDED) }.size == 1
        )
        MatcherAssert.assertThat(
            "First called Periodic Request is executed only",
            works.firstOrNull()?.periodicityInfo != null
        )
    }

    @Test
    fun schedule_OneTime_and_Periodic_request_executes_only_OneTime_request() {
        scheduler.sendRequestsThenCleanUp()
        scheduler.scheduleSendRequests(1L, Constraints.Builder().build())
        val works = workManager.getWorkInfosByTag(SendRequestsWorker.TAG).get()
        MatcherAssert.assertThat(
            "Only one work for sending request are running or it is succeeded",
            works.filter {
                it.state in listOf(
                    WorkInfo.State.SUCCEEDED,
                    WorkInfo.State.RUNNING
                )
            }.size == 1
        )
    }

    @Test
    fun trigger_only_one_time_requests_for_sending_and_cleaning_data() {
        scheduler.sendRequestsThenCleanUp()
        val works = workManager.getWorkInfosByTag(SendRequestsWorker.TAG).get()
        MatcherAssert.assertThat(
            "One time request is triggered",
            works.firstOrNull()?.periodicityInfo == null
        )
    }

    @Test
    fun trigger_cleanup_worker_as_a_chain_work_of_one_time_request() {
        scheduler.sendRequestsThenCleanUp()
        val works = workManager.getWorkInfosByTag(CleanUpWorker.TAG).get()
        MatcherAssert.assertThat(
            "One time request is triggered",
            works.firstOrNull()?.periodicityInfo == null
        )
    }

    @Test
    fun trigger_periodic_work_request() {
        scheduler.scheduleSendRequests(15, Constraints.Builder().build())
        val works = workManager.getWorkInfosByTag(SendRequestsWorker.TAG).get()
        MatcherAssert.assertThat(
            "Periodic work request is triggered",
            works.firstOrNull()?.periodicityInfo != null
        )
    }

    @Test
    fun schedule_and_cancel_all_onetime_work_requests() {
        val tags=listOf(
            SendRequestsWorker.TAG,
            CleanUpWorker.TAG
        )
        val validWorkStates=listOf(WorkInfo.State.RUNNING,WorkInfo.State.ENQUEUED)
        val workQuery=WorkQuery.fromTags(tags)
        var workCount:Int=0
        scheduler.sendRequestsThenCleanUp()
        workCount = workManager.getWorkInfos(workQuery).get().filter { it.state in validWorkStates }.size

        MatcherAssert.assertThat("Work count should be 2", workCount==2)
        tags.forEach { workManager.cancelAllWorkByTag(it) }

        workCount=workManager.getWorkInfos(workQuery).get().filter { it.state in validWorkStates }.size
        MatcherAssert.assertThat("Work count should be 0", workCount==0)
    }

    @Test
    fun schedule_and_cancel_all_periodic_work_requests() {
        val tags=listOf(
            SendRequestsWorker.TAG,
            CleanUpWorker.TAG
        )
        val validWorkStates=listOf(WorkInfo.State.RUNNING,WorkInfo.State.ENQUEUED)
        val workQuery=WorkQuery.fromTags(tags)
        var workCount:Int=0
        scheduler.scheduleSendRequests(15, Constraints.Builder().build())
        workCount = workManager.getWorkInfos(workQuery).get().filter { it.state in validWorkStates }.size

        MatcherAssert.assertThat("Work count should be 1", workCount==1)
        tags.forEach { workManager.cancelAllWorkByTag(it) }

        workCount=workManager.getWorkInfos(workQuery).get().filter { it.state in validWorkStates }.size
        MatcherAssert.assertThat("Work count should be 0", workCount==0)
    }
}