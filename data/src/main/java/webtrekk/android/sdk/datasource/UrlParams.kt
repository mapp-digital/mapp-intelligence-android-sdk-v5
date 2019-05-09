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

package webtrekk.android.sdk.datasource

import webtrekk.android.sdk.datasource.UrlParams.EVENT_NAME

object UrlParams {

    const val WEBTREKK_PARAM = "p"

    const val EVER_ID = "eid"

    const val FORCE_NEW_SESSION = "fns"

    const val APP_FIRST_START = "one"

    const val TIME_ZONE = "tz"

    const val USER_AGENT = "X-WT-UA"

    const val LANGUAGE = "la"

    const val EVENT_NAME = "ct"
}

/**
 * Enum class to specify either the type of the request is a page or an event.
 *
 * This is internal class and should not be used in your application.
 */
enum class RequestType(val value: String) {

    PAGE(""),
    EVENT(EVENT_NAME)
}
