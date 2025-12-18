package com.example.webtrekk.androidsdk.mapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.BACKGROUND_COLOR
import com.example.webtrekk.androidsdk.R
import com.example.webtrekk.androidsdk.databinding.ActivityPageRequestsBinding
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import webtrekk.android.sdk.Param
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk

class PageRequestsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPageRequestsBinding

    private val REQUEST_COUNT=10000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPageRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSendRequests.setOnClickListener {
            resetUI()
            sendRequests()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sendRequests() {
        CoroutineScope(Dispatchers.Default).launch {
            val webtrekk= Webtrekk.getInstance()

            val startTime=System.currentTimeMillis()

            val trackingParams = TrackingParams().apply {
                putAll(mapOf(Param.BACKGROUND_COLOR to "red"))
            }

            for (i in 0..REQUEST_COUNT){
                webtrekk.trackPage(this@PageRequestsActivity,"testAndroid${i}", trackingParams)
                binding.tvInfo.post {
                    binding.tvInfo.text="Current request sent: $i"
                }
            }

            val endTime=System.currentTimeMillis()
            val secondsDuration : Double = ((endTime-startTime)/1000.0)
            binding.tvExecutionTime.text = "Time duration: ${String.format(Locale.US, "%.2f",secondsDuration)}"

            binding.buttonSendRequests.post {
                binding.buttonSendRequests.isEnabled=true
            }
        }
    }

    private fun resetUI(){
        binding.buttonSendRequests.isEnabled=false
        binding.tvInfo.text=""
        binding.tvExecutionTime.text=""
    }
}