@file:UnstableApi

package com.example.webtrekk.androidsdk

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.webtrekk.androidsdk.databinding.ActivityVideoBinding
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.events.MediaEvent
import webtrekk.android.sdk.events.eventParams.MediaParameters
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max


class VideoActivity : AppCompatActivity(), View.OnClickListener {
    val TUNNELING_EXTRA = "tunneling"
    private val TAG = VideoActivity::class.java.simpleName
    private lateinit var binding: ActivityVideoBinding
    private lateinit var player: ExoPlayer
    private var playWhenReady = true
    private var startWindow = 0
    private var startPosition: Long = 0
    private var muted = false
    private var isPlayingNow = true
    private val url = "https://docs.evostream.com/sample_content/assets/bun33s.mp4"
    private val videoName = "Sample video - big buck bunny clip"
    private val trackingInitialized=AtomicBoolean(false)

    private val eventListener: Player.Listener = @UnstableApi object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    Log.i(TAG, "Playback buffering!")
                }

                Player.STATE_IDLE -> {
                    Log.i(TAG, "ExoPlayer idle!")
                }

                Player.STATE_ENDED -> {
                    Log.i(TAG, "Playback ended!")
                    //Stop playback and return to start position
                    val trackingParams = TrackingParams().apply {
                        putAll(
                            mapOf(
                                MediaParam.MEDIA_ACTION to MediaParameters.Action.EOF.code(),
                                MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                                MediaParam.MEDIA_DURATION to (player.duration / 1000).toString(),
                            )
                        )
                    }
                    Webtrekk.getInstance().trackMedia(TAG, videoName, trackingParams)
                    clearStartPosition()
                }

                Player.STATE_READY -> {
                    Log.i(TAG, "Playback ready!")
                    if(!trackingInitialized.getAndSet(true)) {
                        val mediaParameters = MediaParameters(
                            videoName,
                            MediaParameters.Action.INIT.code(),
                            player.currentPosition / 1000,
                            player.duration / 1000
                        )
                        val mediaEvent = MediaEvent(TAG, mediaParameters);
                        Webtrekk.getInstance().trackMedia(mediaEvent)
                    }
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e(TAG, "Error playing video: $error")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            isPlayingNow = isPlaying
            player.isCurrentMediaItemDynamic
            val trackingParams = TrackingParams().apply {
                putAll(
                    mapOf(
                        MediaParam.MEDIA_DURATION to (player.duration / 1000).toString(),
                        MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                        MediaParam.MEDIA_ACTION to if (isPlaying) MediaParameters.Action.PLAY.code() else MediaParameters.Action.PAUSE.code()
                    )
                )
            }
            Webtrekk.getInstance().trackMedia(TAG, videoName, trackingParams)
        }

        override fun onVolumeChanged(volume: Float) {
            super.onVolumeChanged(volume)
            val trackingParams = TrackingParams().apply {
                putAll(
                    mapOf(
                        MediaParam.MEDIA_DURATION to (player.duration / 1000).toString(),
                        MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                        MediaParam.VOLUME to volume.toInt().toString(),
                        MediaParam.MUTE to if (player.isDeviceMuted) "1" else "0"
                    )
                )
            }
            Webtrekk.getInstance().trackMedia(TAG, videoName, trackingParams)
        }
    }

    private val visibilityListener = PlayerView.ControllerVisibilityListener {
        Log.d(TAG, "visibilityChanged: $it")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playerView.setControllerVisibilityListener(visibilityListener)
        binding.playerView.requestFocus()

        clearStartPosition()
    }

    override fun onClick(v: View?) {

    }

    private fun updateStartPosition() {
        player.let {
            playWhenReady = it.playWhenReady
            startWindow = it.currentMediaItemIndex
            startPosition = max(0, it.currentPosition)
        }
    }

    private fun clearStartPosition() {
        playWhenReady = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    private val analyticsListener = object : AnalyticsListener {
        override fun onPositionDiscontinuity(
            eventTime: AnalyticsListener.EventTime,
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            val trackingParams = TrackingParams().apply {
                putAll(
                    mapOf(
                        MediaParam.MEDIA_DURATION to (player.duration / 1000).toString(),
                        MediaParam.MEDIA_POSITION to (eventTime.currentPlaybackPositionMs / 1000).toString(),
                        MediaParam.MEDIA_ACTION to MediaParameters.Action.SEEK.code()
                    )
                )
            }
            Webtrekk.getInstance().trackMedia(TAG, videoName, trackingParams)
        }
    }

    // Internal methods
    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        if (!::player.isInitialized) {
            player = ExoPlayer.Builder(this)
                .build().also {
                    binding.playerView.player = it
                    it.addListener(eventListener)
                    it.setAudioAttributes(
                        AudioAttributes.DEFAULT,  /* handleAudioFocus= */
                        true
                    )
                    it.playWhenReady = playWhenReady
                }
        }

        val mediaItem = MediaItem.Builder().setUri(url).build()
        val mediaSource = DefaultMediaSourceFactory(this)
            .setLiveMaxSpeed(1.02f)
            .setLiveTargetOffsetMs(5000)
            .createMediaSource(mediaItem)
        player.let {
            it.addAnalyticsListener(analyticsListener)
            it.setMediaSources(listOf(mediaSource))
            it.prepare()
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart()")
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            binding.playerView.onResume()
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
        if (Util.SDK_INT <= 23 || !::player.isInitialized) {
            initializePlayer()
            binding.playerView.onResume()
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause()")
        super.onPause()
        val trackingParams = TrackingParams().apply {
            putAll(
                mapOf(
                    MediaParam.MEDIA_DURATION to (player.duration / 1000).toString(),
                    MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                    MediaParam.MEDIA_ACTION to MediaParameters.Action.STOP.code()
                )
            )
        }
        Webtrekk.getInstance().trackMedia(TAG, videoName, trackingParams)
        if (Util.SDK_INT <= 23) {
            binding.playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop()")
        super.onStop()
        if (Util.SDK_INT > 23) {
            binding.playerView.onPause()
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        Log.d(TAG, "releasePlayer")
        val trackingParams = TrackingParams().apply {
            putAll(
                mapOf(
                    MediaParam.MEDIA_DURATION to (player.duration / 1000).toString(),
                    MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                    MediaParam.MEDIA_ACTION to MediaParameters.Action.EOF.code()
                )
            )
        }
        Webtrekk.getInstance().trackMedia(TAG, videoName, trackingParams)

        updateStartPosition()
        player.let {
            it.removeAnalyticsListener(analyticsListener)
            it.stop()
            it.clearMediaItems()
            it.clearVideoSurface()
            it.release()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateStartPosition()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MUTE || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            val trackingParams = TrackingParams().apply {
                putAll(
                    mapOf(
                        MediaParam.MEDIA_DURATION to (player.duration / 1000).toString(),
                        MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                        MediaParam.MUTE to (if (muted) "1" else "0")
                    )
                )
            }
            muted = !muted
            Webtrekk.getInstance().trackMedia(TAG, videoName, trackingParams)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}