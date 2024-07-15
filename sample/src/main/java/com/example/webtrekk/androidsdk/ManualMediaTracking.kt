package com.example.webtrekk.androidsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.databinding.ActivityManualMediaTrackingBinding
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.events.eventParams.MediaParameters
import java.util.Locale

class ManualMediaTracking : AppCompatActivity() {

    private val TAG = ManualMediaTracking::class.java.simpleName
    private lateinit var binding: ActivityManualMediaTrackingBinding

    private val currentTime: Double
        get() {
            val timeString = binding.tietCurrentTime.text.toString()
            var time = timeString.toDoubleOrNull() ?: 0.0
            if (increaseCurrentTime) {
                time += secondsCountToIncrease
                binding.tietCurrentTime.setText(time.toString())
            }
            return time
        }

    private val durationTime: Double
        get() {
            val durationString = binding.tietDuration.text.toString()
            return durationString.toDoubleOrNull() ?: 0.0
        }

    private val videoName: String
        get() {
            val nameString = binding.tietMediaName.text.toString()
            return nameString ?: "Sample test video"
        }

    private val increaseCurrentTime: Boolean
        get() {
            return binding.swIncrementTime.isChecked
        }

    private val secondsCountToIncrease: Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualMediaTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swIncrementTime.text = String.format(
            Locale.US,
            getString(R.string.increment_current_time_by_s_seconds_on_each_action),
            secondsCountToIncrease.toString()
        )

        binding.swIncrementTime.isChecked = false
        binding.tietCurrentTime.setText("0")
        binding.tietDuration.setText("0")

        binding.btnInit.setOnClickListener {
            track(MediaParameters.Action.INIT)
        }

        binding.btnPlay.setOnClickListener {
            track(MediaParameters.Action.PLAY)
        }

        binding.btnPause.setOnClickListener {
            track(MediaParameters.Action.PAUSE)
        }

        binding.btnStop.setOnClickListener {
            track(MediaParameters.Action.STOP)
        }

        binding.btnPosition.setOnClickListener {
            track(MediaParameters.Action.POS)
        }

        binding.btnSeek.setOnClickListener {
            track(MediaParameters.Action.SEEK)
        }

        binding.btnEof.setOnClickListener {
            track(MediaParameters.Action.EOF)
        }
    }

    private fun track(action:MediaParameters.Action){
        val params=TrackingParams().apply {
            putAll(
                mapOf(
                    MediaParam.MEDIA_POSITION to currentTime.toString(),
                    MediaParam.MEDIA_DURATION to durationTime.toString(),
                    MediaParam.MEDIA_ACTION to action.code()
                )
            )
        }
        Webtrekk.getInstance().trackMedia(TAG, videoName, params)
    }
}