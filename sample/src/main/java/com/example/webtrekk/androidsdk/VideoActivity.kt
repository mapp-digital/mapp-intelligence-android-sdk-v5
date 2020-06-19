package com.example.webtrekk.androidsdk

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video.playerView
import okhttp3.internal.userAgent
import kotlin.math.max


class VideoActivity : AppCompatActivity(), View.OnClickListener, PlaybackPreparer,
    PlayerControlView.VisibilityListener {
    val TUNNELING_EXTRA = "tunneling"
    private val TAG = "VideoActivity"
    private var dataSourceFactory: DataSource.Factory? = null
    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private val timeInScreen = 0L
    private var startAutoPlay = false
    private var startWindow = 0
    private var startPosition: Long = 0
    private val eventListener: Player.EventListener = object : Player.EventListener {
        fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            Log.i(TAG, "onTimelineChanged")
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            Log.i(TAG, "onTracksChanged")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Log.i(TAG, "onLoadingChanged")
        }

        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            Log.i(
                TAG,
                "onPlayerStateChanged: playWhenReady = $playWhenReady playbackState = $playbackState"
            )
            when (playbackState) {
                ExoPlayer.STATE_ENDED -> {
                    Log.i(TAG, "Playback ended!")
                    //Stop playback and return to start position
                    clearStartPosition()
                }
                ExoPlayer.STATE_READY -> {

                }
                ExoPlayer.STATE_BUFFERING -> Log.i(TAG, "Playback buffering!")
                ExoPlayer.STATE_IDLE -> Log.i(TAG, "ExoPlayer idle!")
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            Log.i(TAG, "onPlaybackError: " + error.message)
        }

        fun onPositionDiscontinuity() {
            Log.i(TAG, "onPositionDiscontinuity")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        playerView.setControllerVisibilityListener(this)
        playerView.requestFocus()

        val builder = ParametersBuilder( /* context= */this)
        val tunneling = intent.getBooleanExtra(
            TUNNELING_EXTRA,
            false
        )
        if (Util.SDK_INT >= 21 && tunneling) {
            builder.setTunnelingAudioSessionId(C.generateAudioSessionIdV21( /* context= */this))
        }
        trackSelectorParameters = builder.build()
        clearStartPosition()


    }

    private fun buildMediaSource(): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this, userAgent)
        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        return mediaSourceFactory.createMediaSource(Uri.parse("https://firebasestorage.googleapis.com/v0/b/videostreaming-481cd.appspot.com/o/Mobile_Rich_Push.mp4?alt=media&token=3e9156fa-5af7-4344-9efb-7a8c882ab2dc"))
    }

    override fun onClick(v: View?) {

    }

    override fun preparePlayback() {

    }

    override fun onVisibilityChange(visibility: Int) {

    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player!!.playWhenReady
            startWindow = player!!.currentWindowIndex
            startPosition = max(0, player!!.contentPosition)
        }
    }

    private fun updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector?.parameters
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }


    // Internal methods
    private fun initializePlayer() {
        if (player == null) {
            val intent = intent
            mediaSource =buildMediaSource()
            if (mediaSource == null) {
                return
            }
            val trackSelectionFactory =AdaptiveTrackSelection.Factory()
            trackSelector = DefaultTrackSelector( /* context= */this, trackSelectionFactory)
            trackSelector!!.setParameters(trackSelectorParameters!!)
            lastSeenTrackGroupArray = null
            player = SimpleExoPlayer.Builder( /* context= */this)
                .setTrackSelector(trackSelector!!)
                .build()
            player!!.addListener(eventListener)
            player!!.setAudioAttributes(
                AudioAttributes.DEFAULT,  /* handleAudioFocus= */
                true
            )
            player!!.playWhenReady = startAutoPlay
            player!!.addAnalyticsListener(EventLogger(trackSelector))
            playerView.player = player
            playerView.setPlaybackPreparer(this)

        }
        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player!!.seekTo(startWindow, startPosition)
        }
        player!!.prepare(mediaSource!!, !haveStartPosition, false)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            if (playerView != null) {
                playerView.onResume()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            if (playerView != null) {
                playerView.onResume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause()
            }
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause()
            }
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters()
            updateStartPosition()
            player!!.release()
            player = null
            mediaSource = null
            trackSelector = null
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateTrackSelectorParameters()
        updateStartPosition()

    }




}