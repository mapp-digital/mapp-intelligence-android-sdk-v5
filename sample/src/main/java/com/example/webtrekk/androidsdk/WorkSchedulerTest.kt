package com.example.webtrekk.androidsdk

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.webtrekk.androidsdk.databinding.ActivityWorkSchedulerTestBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class WorkSchedulerTest : AppCompatActivity() {

    private val TAG = this::class.java.simpleName
    private lateinit var binding: ActivityWorkSchedulerTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWorkSchedulerTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnScheduleWorks.setOnClickListener {
            val workCount = binding.etWorkCount.text.toString().toIntOrNull() ?: 0
            if (workCount > 0) {
                lifecycleScope.launch(Dispatchers.IO) {
                    for (i in 0 until workCount) {
                        //Webtrekk.getInstance().sendRequestsNowAndClean()
                        MyWorker.enqueue(this@WorkSchedulerTest, i + 1)
                        //MyWorker.enqueuePeriodic(this@WorkSchedulerTest, i + 1)
                        Log.d(TAG, "SCHEDULED WORK(s): ${i + 1}")
                    }
                }
            }
        }
    }
}

internal class MyWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    private val TAG = this::class.java.simpleName
    private val dispatcher = Dispatchers.IO
    override suspend fun doWork(): Result = coroutineScope {
        return@coroutineScope withContext(dispatcher) {
            mutex.withLock {
                val workNumber = inputData.getInt("workNumber", 0)
                Log.d(TAG, "WORK STARTED - $id")
                delay(5000)
                val result = if (workNumber % 3 == 0) Result.failure() else Result.success()
                Log.d(TAG, "WORK FINISHED - $id - Returning $result")
                result
            }
        }
    }

    companion object {
        val mutex = Mutex()
        val workName = "my-worker"
        val periodicWorkName = "my-worker-periodic"
        fun enqueue(context: Context, workNumber: Int) {
            val workManager = WorkManager.getInstance(context)
            val currentWork =
                workManager.getWorkInfosForUniqueWork(workName).get().firstOrNull()
            if (currentWork == null || currentWork.state != WorkInfo.State.RUNNING) {
                val constraints = Constraints.Builder()
                    .build()
                val inputData = Data.Builder()
                    .putInt("workNumber", workNumber)
                    .build()
                val work = OneTimeWorkRequestBuilder<MyWorker>()
                    .setInitialDelay(0, TimeUnit.SECONDS)
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()
                workManager
                    .enqueueUniqueWork(
                        workName,
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        work
                    )
            }
        }

        fun enqueuePeriodic(context: Context, workNumber: Int) {
            val workManager = WorkManager.getInstance(context)
            val currentWork =
                workManager.getWorkInfosForUniqueWork(periodicWorkName).get().firstOrNull()
            if (currentWork == null || currentWork.state != WorkInfo.State.RUNNING) {
                val constraints = Constraints.Builder()
                    .build()
                val inputData = Data.Builder()
                    .putInt("workNumber", workNumber)
                    .build()
                val work = PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.SECONDS)
                    .setInitialDelay(0, TimeUnit.SECONDS)
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()
                workManager
                    .enqueueUniquePeriodicWork(
                        periodicWorkName,
                        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                        work
                    )
            }
        }
    }
}