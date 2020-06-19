package com.example.webtrekk.androidsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import webtrekk.android.sdk.Webtrekk
import kotlinx.android.synthetic.main.activity_crash.*


class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        trackUncaught.setOnClickListener {
            Integer.parseInt("@!#")
        }

        trackCaught.setOnClickListener {
            try {
                Integer.parseInt("@!#")
            } catch (e: Exception) {
                Webtrekk.getInstance().trackException(e)
            }
        }

        trackCustom.setOnClickListener {
            try {
                Integer.parseInt("@!#")
            } catch (e: Exception) {
                Webtrekk.getInstance().trackException("Hello", "I am custom exception :*")
            }
        }
    }
}
