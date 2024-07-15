package com.example.webtrekk.androidsdk

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.databinding.ActivityMedia2Binding
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.events.MediaEvent
import webtrekk.android.sdk.events.eventParams.EventParameters
import webtrekk.android.sdk.events.eventParams.MediaParameters
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MediaActivityExample : AppCompatActivity() {
    private lateinit var binding: ActivityMedia2Binding
    private var webtrekk: Webtrekk? = null
    val trackingParams = TrackingParams()
    private var currentState =
        "" // this variable stores the current state (play/pause/stop)
    private var timerService: ScheduledExecutorService? = null
    val currentPlayProgress: Int
        get() = (MEDIA_LENGTH * (binding.playProgressBar.progress / 100.0)).toInt()

    private fun initMediaTracking() {
        // Tracker initialisieren
        val progress: Int = currentPlayProgress
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_DURATION to MEDIA_LENGTH.toString(),
                MediaParam.MEDIA_POSITION to progress.toString(),
                MediaParam.MEDIA_ACTION to "init"
            )
        )

        Webtrekk.getInstance()
            .trackMedia("MediaActivityExample", "android-demo-media", trackingParams)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webtrekk = Webtrekk.getInstance()
        binding = ActivityMedia2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.playButton.setOnClickListener {
            initMediaTracking()
            val progress: Int = currentPlayProgress
            currentState = "play"
            trackingParams[MediaParam.MEDIA_ACTION] = "play"
            trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
            Webtrekk.getInstance()
                .trackMedia("MediaActivityExample", "android-demo-media", trackingParams)
            startTimerService()
        }
        binding.pauseButton.setOnClickListener {
            val progress: Int = currentPlayProgress
            currentState = "pause"
            trackingParams[MediaParam.MEDIA_ACTION] = "pause"
            trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
            Webtrekk.getInstance()
                .trackMedia("MediaActivityExample", "android-demo-media", trackingParams)
            timerService?.shutdown()
        }
        binding.stopButton.setOnClickListener {
            val progress: Int = currentPlayProgress
            currentState = "stop"
            trackingParams[MediaParam.MEDIA_ACTION] = "stop"
            trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
            Webtrekk.getInstance()
                .trackMedia("MediaActivityExample", "android-demo-media", trackingParams)
            timerService?.shutdown()
        }


        binding.playProgressBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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
                Webtrekk.getInstance()
                    .trackMedia("MediaActivityExample", "android-demo-media", trackingParams)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                val progress: Int = currentPlayProgress
                trackingParams[MediaParam.MEDIA_ACTION] = "seek"
                trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()
                Webtrekk.getInstance()
                    .trackMedia("MediaActivityExample", "android-demo-media", trackingParams)
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
        trackingParams[MediaParam.MEDIA_ACTION] = "eof"
        trackingParams[MediaParam.MEDIA_POSITION] = currentPlayProgress.toString()
        Webtrekk.getInstance()
            .trackMedia("MediaActivityExample", "android-demo-media", trackingParams)
        super.onDestroy()
        timerService?.shutdown()
    }

    fun startTimerService() {
        // start the timer service
        if (timerService == null) {
            timerService = Executors.newSingleThreadScheduledExecutor()
            timerService?.scheduleWithFixedDelay(
                { onPlayIntervalOver() },
                30,
                30,
                TimeUnit.SECONDS
            )
        }

    }

    private fun onPlayIntervalOver() {
        if (currentState == "play") {
            val progress: Int = currentPlayProgress
            trackingParams[MediaParam.MEDIA_ACTION] = "pos"
            trackingParams[MediaParam.MEDIA_POSITION] = progress.toString()

            Webtrekk.getInstance()
                .trackMedia("MediaActivityExample", "android-demo-media", trackingParams)
        }
    }

    private fun trackMediaWithEventParams() {
        // media parameters creates like Map<Int, String> values
        // media parameters keys will be converted to "mg"+map.key and sent in the form
        // {
        //      "mg1":"Custom Param 1",
        //      "mg2":"Custom Param 2"
        //  }
        val mediaCategories = mapOf(1 to "Custom Param 1", 2 to "Custom Param 2")

        val parameters =
            MediaParameters(
                name = "Video Name 1",
                action = MediaParameters.Action.INIT.code(),
                position = 0,
                duration = 120
            ).apply {
                soundIsMuted = false
                soundVolume = 3
                bandwith = 10
                customCategories = mediaCategories
            }

        val eventParameters = EventParameters(
            customParameters = mapOf(
                20 to "Custom Event Parameters 20",
                21 to "Custom Event Parameter 21"
            )
        )

        val mediaEvent = MediaEvent("Media Page 1", parameters)

        mediaEvent.eventParameters=eventParameters

        Webtrekk.getInstance().trackMedia(mediaEvent)
    }

    companion object {
        private const val MEDIA_LENGTH = 360
    }
}