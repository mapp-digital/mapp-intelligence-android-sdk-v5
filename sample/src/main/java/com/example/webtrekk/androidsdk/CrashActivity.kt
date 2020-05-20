package com.example.webtrekk.androidsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.domain.external.ExceptionType
import java.lang.NumberFormatException

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        try {
            Integer.parseInt("@!#")
        } catch (numberFormatException: NumberFormatException) {
            Webtrekk.getInstance().trackException(numberFormatException)
            Webtrekk.getInstance().trackException("Ivan", "Momak")
        }
    }
}
