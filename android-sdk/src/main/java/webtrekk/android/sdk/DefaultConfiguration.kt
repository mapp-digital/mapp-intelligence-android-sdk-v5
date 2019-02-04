package webtrekk.android.sdk

import androidx.work.Constraints
import androidx.work.NetworkType
import java.util.concurrent.TimeUnit

object DefaultConfiguration {

    val logLevel = Logger.Level.BASIC

    const val sendDelay: Long = 15 // minutes

    val timeUnit = TimeUnit.MINUTES

    const val enabledAutoTrack = true

    val workManagerConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()
}
