
package com.example.webtrekk.androidsdk

import android.os.Bundle

import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.activity_media2.*

class MediaActivityExample : AppCompatActivity() {
    private var webtrekk: Webtrekk? = null
    val trackingParams = TrackingParams()
    private var currentState =
        "" // this variable stores the current state (play/pause/stop)
    private var timerService: ScheduledExecutorService? = null
    val currentPlayProgress: Int
        get() = (MEDIA_LENGTH * (playProgressBar!!.progress / 100.0)).toInt()

    private fun initMediaTracking() {
        // Tracker initialisieren
        val progress: Int = currentPlayProgress
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_DURATION to  MEDIA_LENGTH.toString(),
                MediaParam.MEDIA_POSITION to progress.toString(),
                MediaParam.MEDIA_ACTION to "init"
            )
        )

        Webtrekk.getInstance().trackMedia("android-demo-media", trackingParams)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webtrekk = Webtrekk.getInstance()
        setContentView(R.layout.activity_media2)
        playButton.setOnClickListener {
            initMediaTracking()
            val progress: Int = currentPlayProgress
            currentState = "play"
            trackingParams[MediaParam.MEDIA_ACTION] = "play"
            trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
            Webtrekk.getInstance().trackMedia("android-demo-media", trackingParams)
            startTimerService()
        }
        pauseButton.setOnClickListener {
            val progress: Int = currentPlayProgress
            currentState = "pause"
            trackingParams[MediaParam.MEDIA_ACTION] = "pause"
            trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
            Webtrekk.getInstance().trackMedia("android-demo-media", trackingParams)
            timerService?.shutdown()
        }
        stopButton.setOnClickListener {
            val progress: Int = currentPlayProgress
            currentState = "stop"
            trackingParams[MediaParam.MEDIA_ACTION] = "stop"
            trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
            Webtrekk.getInstance().trackMedia("android-demo-media", trackingParams)
            timerService?.shutdown()
        }


        playProgressBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress: Int = currentPlayProgress
                // replace the current tracked action of seekend with the state before seek began
                // so if it was play before set it to play, otherwise set it to pause
                if (currentState != "play") {
                    trackingParams[MediaParam.MEDIA_ACTION] = "pause"
                } else {
                    trackingParams[MediaParam.MEDIA_ACTION] = "play"
                    startTimerService()
                }
                trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
                Webtrekk.getInstance().trackMedia("android-demo-media", trackingParams)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                val progress: Int = currentPlayProgress
                trackingParams[MediaParam.MEDIA_ACTION] = "seek"
                trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
                Webtrekk.getInstance().trackMedia("android-demo-media", trackingParams)
            }

            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                //not interesting for us
            }
        })
    }



    override fun onDestroy() {
        super.onDestroy()
        timerService?.shutdown()
    }

    fun startTimerService() {
        // start the timer service
        if(timerService==null){
            timerService = Executors.newSingleThreadScheduledExecutor()
            timerService?.scheduleWithFixedDelay(
                Runnable { onPlayIntervalOver() },
                30,
                30,
                TimeUnit.SECONDS
            )
        }

    }
    private fun  onPlayIntervalOver() {
        if (currentState == "play") {
            val progress: Int = currentPlayProgress
            trackingParams[MediaParam.MEDIA_ACTION] = "pos"
            trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
            Webtrekk.getInstance().trackMedia("android-demo-media", trackingParams)
        }
    }

    companion object {
        private const val MEDIA_LENGTH = 360
    }
}