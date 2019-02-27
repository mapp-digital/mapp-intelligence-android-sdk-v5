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

package webtrekk.android.sdk

import androidx.annotation.IntRange

// Predefined const params
object Param {

    // Page Data -> PAGE ONLY
    const val INTERNAL_SEARCH = "is"

    // Campaign Data -> BOTH PAGE AND EVENT
    const val MEDIA_CODE = "mc"

    // User Data - > BOTH PAGE AND EVENT
    const val CUSTOMER_ID = "cd"

    // E-Commerce Data -> PAGE ONLY
    const val PRODUCT_NAME = "ba"
    const val PRODUCT_COST = "co"
    const val PRODUCT_CURRENCY = "cr"
    const val PRODUCT_QUANTITY = "qn"
    const val STATUS_OF_SHOPPING_CARD = "st"
    const val ORDER_ID = "oi"
    const val ORDER_VALUE = "ov"
}

// Customizable params, use it with [customParam] function
enum class ParamType(val value: String) {
    PAGE_PARAM("cp"), // PAGE ONLY
    PAGE_CATEGORY("cg"), // PAGE ONLY
    EVENT_PARAM("ck"), // EVENT ONLY
    CAMPAIGN_PARAM("cc"), // BOTH PAGE AND EVENT
    SESSION_PARAM("cs"), // BOTH PAGE AND EVENT
    URM_CATEGORY("uc"), // BOTH PAGE AND EVENT
    ECOMMERCE_PARAM("cb"), // PAGE ONLY
    PRODUCT_CATEGORY("ca") // PAGE ONLY
}

typealias TrackingParams = LinkedHashMap<String, String>

@JvmName("createCustomParam")
fun customParam(paramType: ParamType, @IntRange(from = 0, to = 500) value: Int): String =
    "${paramType.value}$value"

internal enum class RequestType(val value: String) {

    PAGE(""),
    EVENT("ct")
}
