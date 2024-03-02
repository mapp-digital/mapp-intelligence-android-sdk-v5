package com.example.webtrekk.androidsdk


import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.databinding.ActivityStandardVideoBinding
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.Webtrekk


class StandardVideoActivity : AppCompatActivity() {

    private lateinit var binding:ActivityStandardVideoBinding
    private var position = 0
    private lateinit var mediaControls: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStandardVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaControls = MediaController(this)
        try {
            binding.videoView.setMediaController(mediaControls)
            mediaControls.setAnchorView(binding.videoView)
            //myVideoView!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.wt))
            binding.videoView.setVideoURI(Uri.parse("https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8"))
        } catch (ignored: Exception) {
        }

        binding.videoView.requestFocus()
        binding.videoView.setOnPreparedListener { mp ->
            mediaControls.show()
            binding.videoView.seekTo(position)
            if (position == 0) {
                mp.start()
            } else {
                mp.pause()
            }
        }

    }


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("Position", binding.videoView.currentPosition)
        binding.videoView.pause()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        position = savedInstanceState.getInt("Position")
        binding.videoView.seekTo(position)
    }

    override fun onStop() {
        binding.videoView.trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (binding.videoView.trackingParams[MediaParam.MEDIA_DURATION]!!.toInt() / 1000).toString(),
                MediaParam.MEDIA_ACTION to "eof"
            )
        )
        Webtrekk.getInstance().trackMedia("StandardVideoActivity","video name", binding.videoView.trackingParams)
        super.onStop()
    }
}