package com.example.webtrekk.androidsdk

import android.content.Intent
import android.os.Binder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.databinding.ActivityMediaExampleBinding

class MediaExample : AppCompatActivity() {
    private lateinit var binding: ActivityMediaExampleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMediaExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.button.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
            val intent = Intent(this, StandardVideoActivity::class.java)
            startActivity(intent)
        }

        binding.button3.setOnClickListener {
            val intent = Intent(this, MediaActivityExample::class.java)
            startActivity(intent)
        }
    }
}