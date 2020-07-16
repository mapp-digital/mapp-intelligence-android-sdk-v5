package webtrekk.android.sdk.domain.external

import android.content.Context
import org.koin.core.inject
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.core.CustomKoinComponent
import webtrekk.android.sdk.util.END_EX_STRING
import webtrekk.android.sdk.util.EX_ITEM_SEPARATOR
import webtrekk.android.sdk.util.START_EX_STRING
import webtrekk.android.sdk.extension.createString
import webtrekk.android.sdk.util.getFileName
import java.io.BufferedOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

internal class UncaughtExceptionHandler constructor(
    private val defaultHandler: Thread.UncaughtExceptionHandler?,
    private val context: Context
) :
    Thread.UncaughtExceptionHandler, CustomKoinComponent {

    private val logger by inject<Logger>()

    // use AtomicBoolean because value is accessible from other threads.
    private val isHandlingException: AtomicBoolean

    init {
        isHandlingException = AtomicBoolean(false)
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        isHandlingException.set(true)
        try {
            writeUncaughtException(ex)
        } catch (e: Exception) {
            logger.error("An error occurred in the uncaught exception handler" + e.message)
        } finally {
            logger.debug(
                "Completed exception processing." +
                    " Invoking default exception handler."
            )
            defaultHandler?.uncaughtException(thread, ex)
            isHandlingException.set(false)
        }
    }

    private fun writeUncaughtException(ex: Throwable) {
        var outputStream: BufferedOutputStream? = null
        val arrayToSave = arrayOf<String?>(
            START_EX_STRING,
            ex.javaClass.name,
            EX_ITEM_SEPARATOR,
            ex.message,
            EX_ITEM_SEPARATOR,
            ex.cause?.message,
            EX_ITEM_SEPARATOR,
            ex.stackTrace.createString(),
            EX_ITEM_SEPARATOR,
            ex.cause?.stackTrace?.createString(),
            EX_ITEM_SEPARATOR,
            END_EX_STRING
        )
        try {
            outputStream = BufferedOutputStream(
                context.openFileOutput(
                    getFileName(true, context),
                    Context.MODE_APPEND
                )
            )
            for (string in arrayToSave) {
                if (string != null)
                    outputStream.write(string.toByteArray())
                outputStream.write("\n".toByteArray())
            }
            outputStream.flush()
            logger.debug("Uncaught exception saved to file")
        } catch (e: Exception) {
            logger.error("Can't save exception to file: $e")
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
        }
    }

    fun isHandlingException(): Boolean? {
        return isHandlingException.get()
    }
}