package com.example.webtrekk.androidsdk

import android.os.Bundle
import androidx.annotation.Size
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.databinding.ActivityCampaignBinding
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.events.ActionEvent

class CampaignActivity : AppCompatActivity() {

    private val CT = "ct"

    private lateinit var binding: ActivityCampaignBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCampaignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button1.setOnClickListener {

        }
    }

    private fun trackAction(screen: String, action: String){
        val actionEvent = ActionEvent(action)
        val trackingParams = TrackingParams()
        trackingParams.putAll(mapOf(CT to screen))

        logEvent(actionEvent.name, trackingParams)
    }

    private fun trackAction(screen: String, action: String, parameters: Map<String, String>?) {
        val event = ActionEvent(action)
        val trackingParams = TrackingParams()
        trackingParams.putAll(mapOf(CT to screen))

        if (parameters == null) {
            logEvent(event.name, trackingParams)
        } else {
            trackingParams.putAll(parameters)
            logEvent(event.name, trackingParams)
        }
    }

    private fun logEvent(
        @Size(min = 1L, max = 40L) name: String?,
        parameters: Map<String, String>?,
        isCustomPage: Boolean = false
    ) {
        parameters?.let { params ->
            if (isCustomPage) {
                Webtrekk.getInstance().trackCustomPage(name.orEmpty(), params)
            } else {
                Webtrekk.getInstance().trackCustomEvent(name.orEmpty(), params)
            }
        }
    }
}