@file:UnstableApi

package com.example.webtrekk.androidsdk

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.DATA_TYPE_MEDIA
import androidx.media3.common.C.DataType
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSourceUtil
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.hls.DefaultHlsDataSourceFactory
import androidx.media3.exoplayer.hls.HlsDataSourceFactory
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView
import com.example.webtrekk.androidsdk.databinding.ActivityVideoBinding
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import kotlin.math.max


class VideoActivity : AppCompatActivity(), View.OnClickListener {
    val TUNNELING_EXTRA = "tunneling"
    private val TAG = "VideoActivity"
    private lateinit var binding: ActivityVideoBinding
    private lateinit var player: ExoPlayer
    private var playWhenReady = true
    private var startWindow = 0
    private var startPosition: Long = 0
    private var muted = false
    private var isPlayingNow = true
    val trackingParams = TrackingParams()
    private val url = "https://live-par-2-cdn-alt.livepush.io/live/bigbuckbunnyclip/index.m3u8"

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

                    trackingParams.putAll(
                        mapOf(
                            MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                            MediaParam.MEDIA_ACTION to "eof"
                        )
                    )
                    Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
                    clearStartPosition()
                }

                Player.STATE_READY -> {
                    Log.i(TAG, "Playback ready!")
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e(TAG, "Error playing video: $error")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            isPlayingNow = isPlaying
            trackingParams.putAll(
                mapOf(
                    MediaParam.MEDIA_DURATION to (player.duration / 1000).toString(),
                    MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                    MediaParam.MEDIA_ACTION to if (isPlaying) "play" else "pause"
                )
            )
            Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
        }

        override fun onVolumeChanged(volume: Float) {
            super.onVolumeChanged(volume)
            trackingParams.putAll(
                mapOf(
                    MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                    MediaParam.VOLUME to volume.toInt().toString(),
                    MediaParam.MUTE to if (volume.toInt() == 0) "1" else "0"
                )

            )
            Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
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
            trackingParams.putAll(
                mapOf(
                    MediaParam.MEDIA_POSITION to (eventTime.currentPlaybackPositionMs / 1000).toString(),
                    MediaParam.MEDIA_ACTION to "seek"
                )
            )
            Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
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

        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString()
            )
        )
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            binding.playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || !::player.isInitialized) {
            initializePlayer()
            binding.playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "stop"
            )
        )
        Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
        if (Util.SDK_INT <= 23) {
            binding.playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            binding.playerView.onPause()
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        Log.d(TAG, "releasePlayer")
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "eof"
            )
        )
        Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)

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
            trackingParams.putAll(
                mapOf(
                    MediaParam.MEDIA_POSITION to (player.currentPosition / 1000).toString(),
                    MediaParam.MUTE to (if (muted) "1" else "0")
                )
            )
            muted = !muted
            Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}