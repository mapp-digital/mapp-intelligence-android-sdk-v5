package com.example.webtrekk.androidsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings_activity.button
import kotlinx.android.synthetic.main.settings_activity.button2
import kotlinx.android.synthetic.main.settings_activity.button3
import kotlinx.android.synthetic.main.settings_activity.disable_anonymous
import kotlinx.android.synthetic.main.settings_activity.enable_anonymous
import kotlinx.android.synthetic.main.settings_activity.sw_optout
import webtrekk.android.sdk.Webtrekk

class SettingsExample : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)


        button.setOnClickListener {
            val stringIds = BuildConfig.TRACK_IDS
            val domain = BuildConfig.DOMEIN
            val elements: List<String> = stringIds.split(",")
            Webtrekk.getInstance().setIdsAndDomain(elements, domain)
        }

        button2.setOnClickListener {
            Webtrekk.getInstance().setIdsAndDomain(
                listOf("826582930668809"),
                "http://vdestellaaccount01.wt-eu02.net"
            )
        }

        button3.setOnClickListener {
            val stringIds = BuildConfig.TRACK_IDS
            val domain = BuildConfig.DOMEIN
            var elements: MutableList<String> = stringIds.split(",").toMutableList()
            elements.add("826582930668809")
            Webtrekk.getInstance().setIdsAndDomain(elements, domain)

        }

        enable_anonymous.setOnClickListener {
            Webtrekk.getInstance().anonymousTracking(true, setOf("la", "cs804", "cs821", "uc703", "uc709"), generateNewEverId = false)
        }
        disable_anonymous.setOnClickListener {
            Webtrekk.getInstance().anonymousTracking(false, generateNewEverId = false)
        }

        sw_optout.isChecked=Webtrekk.getInstance().hasOptOut()
        sw_optout.setOnCheckedChangeListener { buttonView, isChecked ->
            Webtrekk.getInstance().optOut(isChecked)
        }
    }
}