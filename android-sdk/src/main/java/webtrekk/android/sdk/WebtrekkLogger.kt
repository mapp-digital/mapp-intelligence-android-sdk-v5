package webtrekk.android.sdk

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

internal fun logInfo(message: String) = WebtrekkImpl.getInstance().logger?.info(message)

internal fun logDebug(message: String) = WebtrekkImpl.getInstance().logger?.debug(message)

internal fun logWarn(message: String) = WebtrekkImpl.getInstance().logger?.warn(message)

internal fun logError(message: String) = WebtrekkImpl.getInstance().logger?.error(message)

internal class WebtrekkLogger(level: Logger.Level) : Logger {

    private val tag = "webtrekk"
    private var basicMessage: String? = null

    private val dateFormat = SimpleDateFormat.getDateTimeInstance()
    private val date
        inline get() = dateFormat.format(Date())

    init {
        basicMessage = when (level) {
            Logger.Level.NONE -> null
            Logger.Level.BASIC -> date.toString()
        }
    }

    override fun info(message: String) {
        basicMessage?.let {
            Log.i(tag, "$basicMessage -> $message")
        }
    }

    override fun debug(message: String) {
        basicMessage?.let {
            Log.d(tag, "$basicMessage -> $message")
        }
    }

    override fun warn(message: String) {
        basicMessage?.let {
            Log.w(tag, "$basicMessage -> $message")
        }
    }

    override fun error(message: String) {
        basicMessage?.let {
            Log.wtf(tag, "$basicMessage -> $message")
        }
    }
}
