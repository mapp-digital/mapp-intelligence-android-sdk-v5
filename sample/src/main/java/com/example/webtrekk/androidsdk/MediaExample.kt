package com.example.webtrekk.androidsdk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_media_example.button
import kotlinx.android.synthetic.main.activity_media_example.button2
import kotlinx.android.synthetic.main.activity_media_example.button3

class MediaExample : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_example)


        button.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this, StandardVideoActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this, MediaActivityExample::class.java)
            startActivity(intent)
        }
    }
}