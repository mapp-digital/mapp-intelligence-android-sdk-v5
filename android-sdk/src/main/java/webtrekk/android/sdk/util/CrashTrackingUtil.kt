package webtrekk.android.sdk.util

import android.content.Context
import webtrekk.android.sdk.ExceptionType
import java.io.BufferedReader
import java.io.File

internal const val EX_ITEM_SEPARATE = "wte_item"
internal const val START_EX_STRING = "wte_start"
internal const val END_EX_STRING = "wte_end"
internal const val EX_LINE_SEPARATOR = "|"
internal const val MAX_PARAMETER_NUMBER = 255
internal const val FILE_NAME = "exception.txt"
internal const val NULL_MESSAGE = "unexpected null line"
internal const val NO_NAME_ITEM_SEPARATOR = "no name-message item separator"
internal const val NO_MESSAGE_ITEM_SEPARATOR = "no message-cause message item separator"
internal const val NO_MESSAGE_CAUSE_SEPARATOR = "no cause message-stack item separator"
internal const val NO_END_SEPARATOR = "no end itme separator"
internal const val NO_START_SEPARATOR = "no start item separator"

internal fun ExceptionType.isUncaughtAllowed(): Boolean {
    return when (this) {
        ExceptionType.ALL, ExceptionType.UNCAUGHT, ExceptionType.UNCAUGHT_AND_CAUGHT, ExceptionType.UNCAUGHT_AND_CUSTOM
        -> true
        else -> false
    }
}

internal fun ExceptionType.isCaughtAllowed(): Boolean {
    return when (this) {
        ExceptionType.ALL, ExceptionType.CAUGHT, ExceptionType.UNCAUGHT_AND_CAUGHT, ExceptionType.CUSTOM_AND_CAUGHT
        -> true
        else -> false
    }
}

internal fun ExceptionType.isCustomAllowed(): Boolean {
    return when (this) {
        ExceptionType.ALL, ExceptionType.CUSTOM, ExceptionType.CUSTOM_AND_CAUGHT, ExceptionType.UNCAUGHT_AND_CUSTOM
        -> true
        else -> false
    }
}

internal fun getFileName(fileOnly: Boolean, context: Context): String {
    return if (fileOnly) FILE_NAME else context.filesDir.path + File.separator + FILE_NAME
}

internal class ExceptionWrapper constructor(val name: String, val customMessage: String) : Exception()
internal class IncorrectErrorFileFormatException(message: String) : java.lang.Exception(message)

internal fun Array<StackTraceElement>.createString(): String {
    var stackString = ""
    for (element in this) {
        if (stackString.isNotEmpty()) stackString += EX_LINE_SEPARATOR
        val lineNumber = if (element.className.contains("android.app.") || element.className.contains("java.lang.")) -1 else element.lineNumber
        var stackItem = element.className + "." +
            element.methodName + "(" + element.fileName
        stackItem += if (lineNumber < 0) ")" else ":" + element.lineNumber + ")"
        stackString += if (stackString.length + stackItem.length <= MAX_PARAMETER_NUMBER) stackItem else break
    }
    return stackString
}

internal fun BufferedReader.readParam(): String {
    val line: String? = this.readLine()
    if (line == null) throw IncorrectErrorFileFormatException(NULL_MESSAGE) else return line
}

internal fun BufferedReader.validateLine(validate: String, errorMessage: String) {
    if (this.readLine() != validate)
        throw IncorrectErrorFileFormatException(errorMessage)
}