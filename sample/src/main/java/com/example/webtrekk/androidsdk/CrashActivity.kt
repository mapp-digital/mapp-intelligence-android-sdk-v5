package com.example.webtrekk.androidsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import webtrekk.android.sdk.Webtrekk

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        Integer.parseInt("@!#")
        try {
            Integer.parseInt("@!#")
        } catch (e: Exception) {
            Webtrekk.getInstance().trackException(e)
            Webtrekk.getInstance().trackException("Ivan", "Momak")
        }
    }
}
