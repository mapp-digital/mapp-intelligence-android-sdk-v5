package com.example.webtrekk.androidsdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.settings_activity.*
import webtrekk.android.sdk.Webtrekk

class SettingsExample : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)


        button.setOnClickListener {
            val stringIds = BuildConfig.TRACK_IDS
            val domain = BuildConfig.DOMEIN
            val elements: List<String> = stringIds.split(",")
            Webtrekk.getInstance().changeTrackIdAndDomain(elements, domain)
        }

        button2.setOnClickListener {
            Webtrekk.getInstance().changeTrackIdAndDomain(
                listOf("826582930668809"),
                "http://vdestellaaccount01.wt-eu02.net"
            )

        }

        button3.setOnClickListener {
            val stringIds = BuildConfig.TRACK_IDS
            val domain = BuildConfig.DOMEIN
            var elements: MutableList<String> = stringIds.split(",").toMutableList()
            elements.add("826582930668809")
            Webtrekk.getInstance().changeTrackIdAndDomain(elements, domain)

        }
    }
}