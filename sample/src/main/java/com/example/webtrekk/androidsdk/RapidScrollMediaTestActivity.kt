package com.example.webtrekk.androidsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RapidScrollMediaTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rapid_scroll_media_test)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.rapidScrollMediaTestFragmentContainer,
                    RapidScrollMediaTestFragment()
                )
                .commit()
        }
    }
}
