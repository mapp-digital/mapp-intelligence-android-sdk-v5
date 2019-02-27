package webtrekk.android.sdk

import androidx.work.Constraints
import androidx.work.NetworkType
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object DefaultConfiguration {

    const val AUTO_TRACK_ENABLED = true

    const val SEND_DELAY_VALUE: Long = 15 // minutes

    val TIME_UNIT_VALUE = TimeUnit.MINUTES

    val LOG_LEVEL_VALUE = Logger.Level.BASIC

    val WORK_MANAGER_CONSTRAINTS = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()

    val OKHTTP_CLIENT: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()
}
