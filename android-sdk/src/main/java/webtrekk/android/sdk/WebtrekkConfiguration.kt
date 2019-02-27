package webtrekk.android.sdk

import androidx.work.Constraints
import okhttp3.OkHttpClient
import webtrekk.android.sdk.extension.nullOrEmptyThrowError
import webtrekk.android.sdk.extension.validateEntireList
import java.util.concurrent.TimeUnit

class WebtrekkConfiguration private constructor(
    override val trackIds: List<String>,
    override val trackDomain: String,
    override val logLevel: Logger.Level,
    override val sendDelay: Long,
    override val autoTracking: Boolean,
    override val workManagerConstraints: Constraints,
    override val okHttpClient: OkHttpClient
) : Config {

    class Builder(private val trackIds: List<String>, private val trackDomain: String) {
        private var logLevel = DefaultConfiguration.LOG_LEVEL_VALUE
        private var sendDelay =
            DefaultConfiguration.TIME_UNIT_VALUE.toMillis(DefaultConfiguration.SEND_DELAY_VALUE)
        private var autoTracking = DefaultConfiguration.AUTO_TRACK_ENABLED
        private var constraints = DefaultConfiguration.WORK_MANAGER_CONSTRAINTS
        private var okHttpClientBuilder = DefaultConfiguration.OKHTTP_CLIENT

        @JvmName("setLogLevel")
        fun logLevel(logLevel: Logger.Level) = apply { this.logLevel = logLevel }

        @JvmName("setSendDelay")
        fun sendDelay(timeUnit: TimeUnit = TimeUnit.MINUTES, sendDelay: Long) =
            apply { this.sendDelay = timeUnit.toMillis(sendDelay) }

        fun disableAutoTracking() = apply { this.autoTracking = false }

        @JvmName("setWorkManagerConstraints")
        fun workManagerConstraints(constraints: Constraints) =
            apply { this.constraints = constraints }

        @JvmName("setOkHttpClient")
        fun okHttpClient(okHttpClient: OkHttpClient) =
            apply { this.okHttpClientBuilder = okHttpClient }

        fun build() = WebtrekkConfiguration(
            trackIds.validateEntireList("trackId"),
            trackDomain.nullOrEmptyThrowError("trackDomain"),
            logLevel,
            sendDelay,
            autoTracking,
            constraints,
            okHttpClientBuilder
        )
    }
}
