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

package webtrekk.android.sdk.api

import webtrekk.android.sdk.ParamType
import webtrekk.android.sdk.api.UrlParams.EVENT_NAME
import webtrekk.android.sdk.api.UrlParams.FORM_NAME
import webtrekk.android.sdk.customParam

/**
 * This file contains the predefined URL params that Webtrekk Api expects used in the SDK when sending the requests.
 */
internal object UrlParams {

    const val WEBTREKK_PARAM = "p" // we need this in all requests

    const val EVER_ID = "eid" // we need this in all requests

    const val FORCE_NEW_SESSION = "fns" // on app open (new instance of application is created)

    val APP_FIRST_OPEN = customParam(
        ParamType.SESSION_PARAM,
        821
    ) // we need to send this on new session and when is changed

    const val APP_ONE = "one" // on the app install

    const val TIME_ZONE = "tz" // removed

    const val USER_AGENT = "X-WT-UA" // we need this in all requests

    const val LANGUAGE = "la" // we need this

    const val EVENT_NAME = "ct"

    const val FORM_NAME = "fn"

    const val FORM_FIELD = "ft"

    val ANDROID_API_LEVEL = customParam(
        ParamType.SESSION_PARAM,
        814
    ) // we need to send this on new session and when is changed

    val APP_VERSION_NAME = customParam(
        ParamType.SESSION_PARAM,
        804
    ) // we need to send this on new session and when is changed

    val APP_VERSION_CODE = customParam(
        ParamType.SESSION_PARAM,
        805
    ) // we need to send this on new session and when is changed

    val APP_UPDATED = customParam(
        ParamType.SESSION_PARAM,
        815
    ) // we need to send this on new session and when is changed

    // TODO: Check if this should be here and possibly comment the purpose
    val CRASH_TYPE = customParam(
        ParamType.EVENT_PARAM,
        910
    )

    val CRASH_NAME = customParam(
        ParamType.EVENT_PARAM,
        911
    )

    val CRASH_MESSAGE = customParam(
        ParamType.EVENT_PARAM,
        912
    )

    val CRASH_CAUSE_MESSAGE = customParam(
        ParamType.EVENT_PARAM,
        913
    )

    val CRASH_STACK = customParam(
        ParamType.EVENT_PARAM,
        914
    )

    val CRASH_CAUSE_STACK = customParam(
        ParamType.EVENT_PARAM,
        915
    )
}

/**
 * Enum class represents the type of the request. For now the request is either Page request or Event request.
 */
internal enum class RequestType(val value: String) {

    PAGE(""),
    EVENT(EVENT_NAME),
    FORM(FORM_NAME)
}
