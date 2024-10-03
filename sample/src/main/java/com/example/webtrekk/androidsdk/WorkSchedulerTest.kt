package com.example.webtrekk.androidsdk

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.webtrekk.androidsdk.databinding.ActivityWorkSchedulerTestBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import webtrekk.android.sdk.Webtrekk

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
                for (i in 0 until workCount) {
                    Webtrekk.getInstance().sendRequestsNowAndClean()
                    //MyWorker.enqueue(this)
                    Log.d(TAG, "SCHEDULED WORK: ${i + 1}")
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
            Log.d(TAG, "WORK STARTED - ${id}")
            delay(5000)
            Result.success()
        }
    }

    companion object {
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .build()
            val work = OneTimeWorkRequestBuilder<MyWorker>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueue(work)
        }
    }
}