package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.extension.addNotNull
import webtrekk.android.sdk.extension.formatNumber

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class MediaParameters(
    var name: String,
    var action: String,
    var position: Number,
    val duration: Number
) : BaseEvent {
    var bandwith: Number? = null
    var soundIsMuted: Boolean? = null
    var soundVolume: Int? = null
    var customCategories: Map<Int, String> = emptyMap()

    enum class Action(private val code: String) : Code {
        INIT("init"),
        PLAY("play"),
        PAUSE("pause"),
        STOP("stop"),
        SEEK("seek"),
        POS("pos"),
        EOF("eof");

        override fun code(): String {
            return this.code
        }
    }

    interface Code {
        fun code(): String
    }

    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        if (!customCategories.isNullOrEmpty()) {
            customCategories.forEach { (key, value) ->
                map.addNotNull("${MediaParam.MEDIA_CATEGORY}$key", value)
            }
        }
        // map.addNotNull(MediaParam.NAME, name)
        map.addNotNull(MediaParam.MEDIA_ACTION, action)
        map.addNotNull(MediaParam.MEDIA_POSITION, position.formatNumber())
        map.addNotNull(MediaParam.MEDIA_DURATION, duration.formatNumber())
        if (soundIsMuted != null)
            map.addNotNull(MediaParam.MUTE, if (soundIsMuted == true) "1" else "0")
        if (soundVolume != null)
            map.addNotNull(MediaParam.VOLUME, soundVolume.formatNumber())
        if (bandwith != null)
            map.addNotNull(MediaParam.BANDWIDTH, bandwith.formatNumber())

        return map
    }
}