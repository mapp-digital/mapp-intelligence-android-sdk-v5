
package com.example.webtrekk.androidsdk

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk


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
    }

    override fun pause() {
        super.pause()
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "pause"
            )
        )
        Webtrekk.getInstance().trackMedia("video name", trackingParams)
    }

    override fun start() {
        super.start()
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "init"
            )
        )
        Webtrekk.getInstance().trackMedia("video name", trackingParams)

    }

    override fun stopPlayback() {
        super.stopPlayback()
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "stop"
            )
        )
        Webtrekk.getInstance().trackMedia("video name", trackingParams)
    }

    override fun seekTo(msec: Int) {
        super.seekTo(msec)
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "seek"
            )
        )
        Webtrekk.getInstance().trackMedia("video name", trackingParams)
    }


}