/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package webtrekk.android.sdk.extension

import java.io.BufferedReader
import java.io.IOException
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import okhttp3.Request
import okio.Buffer
import org.json.JSONObject
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.data.model.FormField
import webtrekk.android.sdk.util.EX_LINE_SEPARATOR
import webtrekk.android.sdk.util.IncorrectErrorFileFormatException
import webtrekk.android.sdk.util.MAX_PARAMETER_NUMBER
import webtrekk.android.sdk.util.NULL_MESSAGE
import kotlin.collections.HashMap

/**
 * This file contains helper general extension functions.
 */

/**
 * Validates if a property is empty or null, will throw [error] indicating the missing element.
 */
inline fun <reified T : Any> T?.nullOrEmptyThrowError(propertyName: T): T {
    when (this) {
        is String? -> if (this.isNullOrBlank())
            error("$propertyName is missing in the configurations. $propertyName is required in the configurations.")

        is List<*>? -> if (this.isEmpty())
            error("$propertyName is missing in the configurations. $propertyName is required in the configurations.")
    }

    return this
}

/**
 * Validates that a list is not empty.
 */
internal fun <T : Any> List<T>?.validateEntireList(propertyName: Any): List<T> {
    this.nullOrEmptyThrowError(propertyName)

    this?.forEach {
        it.nullOrEmptyThrowError(propertyName)
    }

    return this as List<T>
}

internal fun String.encodeToUTF8(): String = URLEncoder.encode(this, "UTF-8")

internal fun String.jsonToMap(): Map<String, String> {
    val map = HashMap<String, String>()
    val obj = JSONObject(this)
    val keysItr = obj.keys()
    while (keysItr.hasNext()) {
        val key = keysItr.next()
        val value = obj.getString(key)
        map[key] = value
    }
    return map
}

internal fun MutableMap<String, String>.addNotNull(key: String, value: String? = "") {
    if (value != null && value.isNotBlank() && key.isNotBlank())
        this[key] = value
}

internal fun MutableMap<String, String>.addNotNull(key: String, value: Number?) {
    if (value != null && key.isNotBlank()) {
        this[key] = value.formatNumber()
    }
}

internal fun Number?.formatNumber(): String {
    if (this != null) {
        val doubleValue = toDouble()
        val result = doubleValue - doubleValue.toInt()
        return if (result != 0.0) {
            val df =
                DecimalFormat("#.############", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
            df.maximumFractionDigits = 8
            df.format(doubleValue)
        } else {
            val df = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
            df.maximumFractionDigits = 0
            df.isDecimalSeparatorAlwaysShown = false
            df.format(toInt())
        }
    }
    return ""
}

internal fun <T> Sequence<T>.batch(n: Int): Sequence<List<T>> {
    return BatchingSequence(this, n)
}

private class BatchingSequence<T>(val source: Sequence<T>, val batchSize: Int) : Sequence<List<T>> {
    override fun iterator(): Iterator<List<T>> = object : AbstractIterator<List<T>>() {
        val iterate = if (batchSize > 0) source.iterator() else emptyList<T>().iterator()
        override fun computeNext() {
            if (iterate.hasNext()) setNext(iterate.asSequence().take(batchSize).toList())
            else done()
        }
    }
}

internal fun Request.stringifyRequestBody(): String {
    return try {
        val copy: Request = newBuilder().build()
        val buffer = Buffer()
        copy.body!!.writeTo(buffer)
        buffer.readUtf8()
    } catch (e: IOException) {
        return "did not work"
    }
}

internal fun List<FormField>.toRequest(): String {
    var request = ""
    forEach { formField ->
        request += formField.toRequest() + ";"
    }
    request.dropLast(1)
    return request
}

internal fun Boolean.toInt() = if (this) 1 else 0
internal fun BufferedReader.readParam(): String {
    val line: String? = this.readLine()
    if (line == null) throw IncorrectErrorFileFormatException(NULL_MESSAGE) else return line
}

internal fun BufferedReader.validateLine(validate: String, errorMessage: String) {
    if (this.readLine() != validate)
        throw IncorrectErrorFileFormatException(errorMessage)
}

internal fun Array<StackTraceElement>.createString(): String {
    var stackString = ""
    for (element in this) {
        if (stackString.isNotEmpty()) stackString += EX_LINE_SEPARATOR
        val lineNumber =
            if (element.className.contains("android.app.") || element.className.contains("java.lang.")) -1 else element.lineNumber
        var stackItem = element.className + "." +
                element.methodName + "(" + element.fileName
        stackItem += if (lineNumber < 0) ")" else ":" + element.lineNumber + ")"
        stackString += if (stackString.length + stackItem.length <= MAX_PARAMETER_NUMBER) stackItem else break
    }
    return stackString
}

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