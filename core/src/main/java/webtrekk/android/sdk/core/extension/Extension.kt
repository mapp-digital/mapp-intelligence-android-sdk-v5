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

package webtrekk.android.sdk.core.extension

import java.net.URLEncoder

inline fun <reified T : Any> T?.nullOrEmptyThrowError(propertyName: T): T {
    when (this) {
        is String? -> if (this.isNullOrBlank())
            error("$propertyName is missing in the configurations. $propertyName is required in the configurations.")

        is List<*>? -> if (this.isNullOrEmpty())
            error("$propertyName is missing in the configurations. $propertyName is required in the configurations.")
    }

    return this as T
}

fun <T : Any> List<T>?.validateEntireList(propertyName: Any): List<T> {
    this.nullOrEmptyThrowError(propertyName)

    this?.forEach {
        it.nullOrEmptyThrowError(propertyName)
    }

    return this as List<T>
}

fun String.encodeToUTF8(): String = URLEncoder.encode(this, "UTF-8")
