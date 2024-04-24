package com.example.webtrekk.androidsdk

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.databinding.SettingsActivityBinding
import webtrekk.android.sdk.Webtrekk

class SettingsExample : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=SettingsActivityBinding.inflate(layoutInflater)
        setContentView(R.layout.settings_activity)


        binding.button.setOnClickListener {
            val stringIds = BuildConfig.TRACK_IDS
            val domain = BuildConfig.DOMEIN
            val elements: List<String> = stringIds.split(",")
            Webtrekk.getInstance().setIdsAndDomain(elements, domain)
        }

        binding.button2.setOnClickListener {
            Webtrekk.getInstance().setIdsAndDomain(
                listOf("826582930668809"),
                "http://vdestellaaccount01.wt-eu02.net"
            )
        }

        binding.button3.setOnClickListener {
            val stringIds = BuildConfig.TRACK_IDS
            val domain = BuildConfig.DOMEIN
            var elements: MutableList<String> = stringIds.split(",").toMutableList()
            elements.add("826582930668809")
            Webtrekk.getInstance().setIdsAndDomain(elements, domain)

        }

        binding.enableAnonymous.setOnClickListener {
            Webtrekk.getInstance().anonymousTracking(
                true,
                setOf("la", "cs804", "cs821", "uc703", "uc709"))
            updateAnonymousTrackingStatus()
        }
        binding.disableAnonymous.setOnClickListener {
            Webtrekk.getInstance().anonymousTracking(false)
            updateAnonymousTrackingStatus()
        }

        binding.swOptout.isChecked = Webtrekk.getInstance().hasOptOut()
        binding.swOptout.setOnCheckedChangeListener { buttonView, isChecked ->
            Webtrekk.getInstance().optOut(isChecked)
        }
        updateAnonymousTrackingStatus()
    }

    private fun updateAnonymousTrackingStatus() {
        val status = "Anonymous status:" + if (Webtrekk.getInstance()
                .isAnonymousTracking()
        ) "ENABLED" else "DISABLED"
        findViewById<TextView>(R.id.tvAnonymousStatus).text = status

    }
}