package com.example.webtrekk.androidsdk.tracking

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.databinding.ActivityOrdersTrackingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersTrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrdersTrackingBinding
    private val coroutineContext = Dispatchers.IO
    private val coroutineScope = CoroutineScope(coroutineContext + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateOrders.setOnClickListener {
            createOrders()
        }
    }

    private fun createOrders() {
        binding.btnCreateOrders.isEnabled = false
        val testData = OrdersTestData()
        val ordersCount: Int = binding.etRequestsCount.text.toString().parseInt()
        binding.progressBar.max = ordersCount
        binding.tvOrdersCreatedInfo.visibility = View.GONE
        coroutineScope.launch {
            for (i in 0 until ordersCount) {
                testData.createOrder()
                withContext(Dispatchers.Main) {
                    binding.progressBar.progress = i + 1
                }
                delay(50)
            }
            withContext(Dispatchers.Main) {
                binding.btnCreateOrders.isEnabled = true
                binding.progressBar.progress = 0
                binding.tvOrdersCreatedInfo.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel("Activity closed!!!")
    }

    fun String?.parseInt(): Int {
        val text = this ?: return 0
        try {
            return text.toInt()
        } catch (e: Exception) {
            return 0
        }
    }
}