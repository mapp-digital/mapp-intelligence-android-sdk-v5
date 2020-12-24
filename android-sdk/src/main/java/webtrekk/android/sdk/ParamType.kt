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

import androidx.annotation.RestrictTo

/**
 * This file, contains all the predefined custom params. and helper function that helps you create your
 * own custom params depend on their names/keys in the analytics.
 *
 * A sample usage:
 *
 * // Define first your custom params (names/keys) as extension properties of [Param], for consistency and code convention.
 * val Param.BACKGROUND_COLOR
 *      inline get() = customParam(ParamType.PAGE_PARAM, 100)
 *
 * val Param.TRACKING_LOCATION
 *      inline get() = customParam(ParamType.SESSION_PARAM, 10)
 *
 * // Add your custom params to [TrackingParams] object, which is a map of custom params you defined and their values.
 * val trackingParams = TrackingParams()
 * trackingParams.putAll(
 *      mapOf(
 *          Param.INTERNAL_SEARCH to "search",
 *          Param.BACKGROUND_COLOR to "blue",
 *          Param.TRACKING_LOCATION to "my new location"
 *      )
 * )
 *
 * // Send your trackingParams object to Webtrekk
 * Webtrekk.getInstance().trackCustomPage("Product Page", trackingParams)
 *
 * [Param] class contains predefined custom params names/keys, which you don't need to append any more names to it.
 */
object Param {

    /**
     * A predefined custom param key, used for PAGE tracking only.
     *
     * Sending this custom param for event tracking, its value will be ignored in the analytics.
     */
    const val INTERNAL_SEARCH = "is"

    /**
     * A predefined custom param key, could be used for both PAGE and EVENT tracking.
     *
     * Mostly used for tracking campaign data.
     */
    const val MEDIA_CODE = "mc"

    /**
     * A predefined custom param key, could be used for both PAGE and EVENT tracking.
     *
     * Mostly used for tracking user data.
     */
    const val CUSTOMER_ID = "cd"

    /**
     * A predefined custom params keys, used for PAGE tracking only.
     *
     * Sending any of these custom params for event tracking, their values will be ignored in the analytics.
     *
     * Mostly used for tracking e-commerce data.
     */
    const val PRODUCT_NAME = "ba"
    const val PRODUCT_COST = "co"
    const val PRODUCT_CURRENCY = "cr"
    const val PRODUCT_QUANTITY = "qn"
    const val STATUS_OF_SHOPPING_CARD = "st"
    const val ORDER_ID = "oi"
    const val ORDER_VALUE = "ov"
}

object MediaParam {
    const val MEDIA_CATEGORY = "mg"
    const val MEDIA_ACTION = "mk"
    const val MEDIA_POSITION = "mt1"
    const val MEDIA_DURATION = "mt2"
    const val BANDWIDTH = "bw"
    const val VOLUME = "vol"
    const val MUTE = "mut"
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object InternalParam {
    /**
     * Only for dev team, don't use
     */
    const val MEDIA_CODE_PARAM_EXCHANGER = "mc_param_changer"
    const val WT_MC_DEFAULT = "wt_mc"
}

/**
 * This enum class contains predefined custom params names/keys that you must append some names/numbers to their keys
 * depend on what are their actual names in the analytics.
 *
 * Use alongside [customParam] to create a custom param.
 *
 * A sample usage:
 *
 * val Param.BACKGROUND_COLOR
 *      inline get() = customParam(ParamType.PAGE_PARAM, 100)
 */
enum class ParamType(val value: String) {

    /**
     * Those custom params are used for page tracking only.
     *
     * Using them in event tracking, their values will be ignored in the analytics.
     */
    PAGE_PARAM("cp"),
    PAGE_CATEGORY("cg"),
    ECOMMERCE_PARAM("cb"),
    PRODUCT_CATEGORY("ca"),

    /**
     * This custom param is used for event tracking only.
     *
     * Using it in page tracking, its value will be ignored in the analytics.
     */
    EVENT_PARAM("ck"),

    /**
     * Those custom params can be used for both page and event tracking.
     */
    CAMPAIGN_PARAM("cc"),
    SESSION_PARAM("cs"),
    URM_CATEGORY("uc")
}

/**
 * A type alias name for the map of custom params and their values that you like to send for tracking.
 *
 * A sample usage:
 *
 * val trackingParams = TrackingParams()
 * trackingParams.putAll(
 *      mapOf(
 *          Param.INTERNAL_SEARCH to "search",
 *          Param.BACKGROUND_COLOR to "blue",
 *          Param.TRACKING_LOCATION to "my new location"
 *      )
 * )
 */
typealias TrackingParams = LinkedHashMap<String, String>

/**
 * A helper function, that is used to create a custom param name/key depends on the real custom param name in the analytics.
 *
 * A sample usage:
 *
 * val customParam = customParam(EVENT_PARAM, 15)
 */
@JvmName("createCustomParam")
fun customParam(paramType: ParamType, value: Int): String =
    "${paramType.value}$value"
