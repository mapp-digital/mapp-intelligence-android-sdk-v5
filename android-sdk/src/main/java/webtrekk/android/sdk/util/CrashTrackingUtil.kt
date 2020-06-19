package webtrekk.android.sdk.util

import android.content.Context
import java.io.File

internal class ExceptionWrapper constructor(val name: String, val customMessage: String) : Exception()

internal class IncorrectErrorFileFormatException(message: String) : java.lang.Exception(message)

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

internal fun getFileName(fileOnly: Boolean, context: Context): String {
    return if (fileOnly) FILE_NAME else context.filesDir.path + File.separator + FILE_NAME
}