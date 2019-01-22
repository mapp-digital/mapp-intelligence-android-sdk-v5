package com.example.webtrekk.androidsdk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.model.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startDetailsActivity.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            startActivity(intent)
        }

        val trackingParams = TrackingParams()

        trackingParams.putAll(
            mapOf(
                Param.INTERNAL_SEARCH to "search",
                Param.BACKGROUND_COLOR to "blue",
                Param.TRACKING_LOCATION to "my new location"
            )
        )

        Webtrekk.getInstance().trackPage("First page", trackingParams)
    }
}
