package webtrekk.android.sdk

import java.util.concurrent.TimeUnit

internal val LOG_LEVEL_DEFAULT = Logger.Level.BASIC

internal const val SEND_DELAY_DEFAULT: Long = 15 // minutes

internal val TIME_UNIT_DEFAULT = TimeUnit.MINUTES

internal const val ENABLED_AUTO_TRACKING_DEFAULT = true

interface Config {

    val trackIds: List<String>

    val trackDomain: String

    val logLevel: Logger.Level

    val sendDelay: Long

    val autoTracking: Boolean
}
