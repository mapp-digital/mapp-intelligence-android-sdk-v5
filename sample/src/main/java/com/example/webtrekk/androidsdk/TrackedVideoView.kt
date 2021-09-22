package com.example.webtrekk.androidsdk

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView
import webtrekk.android.sdk.*


class TrackedVideoView : VideoView {
    private var webtrekk: Webtrekk? = null
    val trackingParams = TrackingParams()

    constructor(context: Context?) : super(context) {
        initMediaFile()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        initMediaFile()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        initMediaFile()
    }

    fun initMediaFile() {
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_DURATION to duration.toString(),
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString()

            )
        )

        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "init",
                ParamType.EVENT_PARAM.value to "EVENT_PARAM"


            )
        )
        Webtrekk.getInstance().trackMedia("TrackedVideoView","video name", trackingParams)
    }

    override fun pause() {
        super.pause()
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "pause"
            )
        )
        Webtrekk.getInstance().trackMedia("TrackedVideoView","video name", trackingParams)
    }


    override fun start() {
        super.start()
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "play"
            )
        )
        Webtrekk.getInstance().trackMedia("TrackedVideoView","video name", trackingParams)

    }

    override fun stopPlayback() {
        super.stopPlayback()
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "stop"
            )
        )
        Webtrekk.getInstance().trackMedia("TrackedVideoView","video name", trackingParams)
    }


    override fun seekTo(msec: Int) {
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "seek"
            )
        )
        Webtrekk.getInstance().trackMedia("TrackedVideoView","video name", trackingParams)
        super.seekTo(msec)
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "play"
            )
        )
        Webtrekk.getInstance().trackMedia("TrackedVideoView","video name", trackingParams)
    }


}