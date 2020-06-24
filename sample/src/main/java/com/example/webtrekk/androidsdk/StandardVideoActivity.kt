package com.example.webtrekk.androidsdk


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_standard_video.videoView


class StandardVideoActivity : AppCompatActivity() {

    private var myVideoView: TrackedVideoView? = null
    private var position = 0
    private var mediaControls: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_video)
        myVideoView=videoView
        mediaControls = MediaController(this)
        try {
            myVideoView!!.setMediaController(mediaControls)
            mediaControls!!.setAnchorView(myVideoView)
            myVideoView!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.wt))
        } catch (e: Exception) {
        }

        myVideoView!!.requestFocus()
        myVideoView!!.setOnPreparedListener { mp ->
            mediaControls!!.show()
            myVideoView!!.seekTo(position)
            if (position == 0) {
                mp.start()
            } else {
                mp.pause()
            }
        }

    }


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("Position", myVideoView!!.currentPosition)
        myVideoView!!.pause()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        position = savedInstanceState.getInt("Position")
        myVideoView!!.seekTo(position)
    }
}