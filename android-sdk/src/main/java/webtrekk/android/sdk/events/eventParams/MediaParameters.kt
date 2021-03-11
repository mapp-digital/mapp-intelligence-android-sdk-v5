package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class MediaParameters(
    var name: String = "",
    var action: String = "",
    var position: Number = 0,
    val duration: Number = 0
) {
    var bandwith: Number? = null
    var soundIsMuted: Boolean? = null
    var soundVolume: Number? = null
    var customParameters: Map<Int, String> = emptyMap()

    enum class Action(private val code: String) : Code {
        PLAY("play");

        override fun code(): String {
            return this.code
        }
    }

    interface Code {
        fun code(): String
    }
}