package com.example.webtrekk.androidsdk.tracking

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.R
import kotlinx.android.synthetic.main.activity_orders_tracking.btnCreateOrders
import kotlinx.android.synthetic.main.activity_orders_tracking.etRequestsCount
import kotlinx.android.synthetic.main.activity_orders_tracking.progressBar
import kotlinx.android.synthetic.main.activity_orders_tracking.tvOrdersCreatedInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersTrackingActivity : AppCompatActivity() {
    private val coroutineContext = Dispatchers.IO
    private val coroutineScope = CoroutineScope(coroutineContext + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_tracking)

        btnCreateOrders.setOnClickListener {
            createOrders()
        }
    }

    private fun createOrders() {
        btnCreateOrders.isEnabled = false
        val testData = OrdersTestData()
        val ordersCount: Int = etRequestsCount.text.toString().parseInt()
        progressBar.max = ordersCount
        tvOrdersCreatedInfo.visibility = View.GONE
        coroutineScope.launch {
            for (i in 0 until ordersCount) {
                testData.createOrder()
                withContext(Dispatchers.Main) {
                    progressBar.progress = i + 1
                }
                delay(500)
            }
            withContext(Dispatchers.Main) {
                btnCreateOrders.isEnabled = true
                progressBar.progress = 0
                tvOrdersCreatedInfo.visibility = View.VISIBLE
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