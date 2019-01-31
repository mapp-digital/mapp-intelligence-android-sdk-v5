package webtrekk.android.sdk

import androidx.work.Constraints

interface Config {

    val trackIds: List<String>

    val trackDomain: String

    val logLevel: Logger.Level

    val sendDelay: Long

    val autoTracking: Boolean

    val workManagerConstraints: Constraints
}
