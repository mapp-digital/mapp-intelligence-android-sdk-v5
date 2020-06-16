package webtrekk.android.sdk.util

import android.content.Context
import webtrekk.android.sdk.ExceptionType
import java.io.File

internal const val EX_ITEM_SEPARATE = "wte_item"
internal const val START_EX_STRING = "wte_start"
internal const val END_EX_STRING = "wte_end"
internal const val EX_LINE_SEPARATOR = "|"
internal const val MAX_PARAMETER_NUMBER = 255
internal const val FILE_NAME = "exception.txt"

internal fun isUncaughtAllowed(exceptionType: ExceptionType): Boolean {
    when (exceptionType) {
        ExceptionType.ALL, ExceptionType.UNCAUGHT, ExceptionType.UNCAUGHT_AND_CAUGHT, ExceptionType.UNCAUGHT_AND_CUSTOM
        -> return true
        else -> return false
    }
}

internal fun isCaughtAllowed(exceptionType: ExceptionType): Boolean {
    when (exceptionType) {
        ExceptionType.ALL, ExceptionType.CAUGHT, ExceptionType.UNCAUGHT_AND_CAUGHT, ExceptionType.CUSTOM_AND_CAUGHT
        -> return true
        else -> return false
    }
}

internal fun isCustomAllowed(exceptionType: ExceptionType): Boolean {
    when (exceptionType) {
        ExceptionType.ALL, ExceptionType.CUSTOM, ExceptionType.CUSTOM_AND_CAUGHT, ExceptionType.UNCAUGHT_AND_CUSTOM
        -> return true
        else -> return false
    }
}

internal fun getFileName(fileOnly: Boolean, context: Context): String? {
    return if (fileOnly) FILE_NAME else context.filesDir.path + File.separator + FILE_NAME
}

internal class ExceptionWrapper constructor(val name: String, val customMessage: String) : Exception()

internal fun Array<StackTraceElement>.createString(): String {
    var stackString = ""
    for (element in this) {
        if (stackString.isNotEmpty()) stackString += "|"
        val lineNumber = if (element.className.contains("android.app.") || element.className.contains("java.lang.")) -1 else element.lineNumber
        var stackItem = element.className + "." +
            element.methodName + "(" + element.fileName
        stackItem += if (lineNumber < 0) ")" else ":" + element.lineNumber + ")"
        stackString += if (stackString.length + stackItem.length <= MAX_PARAMETER_NUMBER) stackItem else break
    }
    return stackString
}