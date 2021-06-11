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
import webtrekk.android.sdk.BaseParam.COMMERCE_PARAM
import webtrekk.android.sdk.BaseParam.URM_CATEGORY

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
    @Deprecated(message = "It is part of PAGE_PARAMS")
    const val INTERNAL_SEARCH = "is"

    /**
     * A predefined custom param key, could be used for both PAGE and EVENT tracking.
     *
     * Mostly used for tracking campaign data.
     */
    @Deprecated(message = "It is part of CAMPAIGN_PARAMS")
    const val MEDIA_CODE = "mc"

    /**
     * A predefined custom param key, could be used for both PAGE and EVENT tracking.
     *
     * Mostly used for tracking user data.
     */
    @Deprecated(message = "It is part of USER_CATEGORIES_PARAMS")
    const val CUSTOMER_ID = "cd"

    /**
     * A predefined custom params keys, used for PAGE and EVENT tracking.
     *
     * Sending any of these custom params for event tracking, their values will be ignored in the analytics.
     *
     * Mostly used for tracking e-commerce data.
     */
    @Deprecated(message = "It is part of E_COMMERCE_PARAM")
    const val PRODUCT_NAME = "ba"
    @Deprecated(message = "It is part of E_COMMERCE_PARAM")
    const val PRODUCT_COST = "co"
    @Deprecated(message = "It is part of E_COMMERCE_PARAM")
    const val PRODUCT_CURRENCY = "cr"
    @Deprecated(message = "It is part of E_COMMERCE_PARAM")
    const val PRODUCT_QUANTITY = "qn"
    @Deprecated(message = "It is part of E_COMMERCE_PARAM")
    const val STATUS_OF_SHOPPING_CARD = "st"
    @Deprecated(message = "It is part of E_COMMERCE_PARAM")
    const val ORDER_ID = "oi"
    @Deprecated(message = "It is part of E_COMMERCE_PARAM")
    const val ORDER_VALUE = "ov"
    val MEDIA_PARAMS: MediaParam = MediaParam
    val USER_CATEGORIES_PARAMS = UserCategoriesParam
    val E_COMMERCE_PARAMS = ECommerceParam
    val PAGE_PARAMS = PageParam
    val EVENT_PARAMS = EventParam
    val SESSION_PARAMS = SessionParam
    val CAMPAIGN_PARAMS = CampaignParam
}

object MediaParam {
    const val MEDIA_CATEGORY = "mg"
    const val MEDIA_ACTION = "mk"
    const val MEDIA_POSITION = "mt1"
    const val MEDIA_DURATION = "mt2"
    const val BANDWIDTH = "bw"
    const val VOLUME = "vol"
    const val MUTE = "mut"
    const val NAME = "mi"
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
object UserCategoriesParam {
    const val BIRTHDAY = URM_CATEGORY + 707
    const val CITY = URM_CATEGORY + 709
    const val COUNTRY = URM_CATEGORY + 708
    const val EMAIL_ADDRESS = URM_CATEGORY + 700
    const val EMAIL_RECEIVER_ID = URM_CATEGORY + 701
    const val FIRST_NAME = URM_CATEGORY + 703
    const val GENDER = URM_CATEGORY + 706
    const val CUSTOMER_ID = "cd"
    const val LAST_NAME = URM_CATEGORY + 704
    const val NEW_SELLER_SUBSCRIBED = URM_CATEGORY + 702
    const val PHONE_NUMBER = URM_CATEGORY + 705
    const val STREET = URM_CATEGORY + 711
    const val STREET_NUMBER = URM_CATEGORY + 712
    const val ZIP_CODE = URM_CATEGORY + 710
}
@RestrictTo(RestrictTo.Scope.LIBRARY)
object BaseParam {
    const val COMMERCE_PARAM = "cb"
    const val URM_CATEGORY = "uc"
    const val SESSION_PARAM = "cs"
    const val EVENT_PARAM = "ck"
    const val PAGE_PARAM = "cp"
    const val PAGE_CATEGORY = "cg"
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
object ECommerceParam {
    const val RETURNING_OR_NEW_CUSTOMER = COMMERCE_PARAM + 560
    const val RETURN_VALUE = COMMERCE_PARAM + 561
    const val CANCELLATION_VALUE = COMMERCE_PARAM + 562
    const val COUPON_VALUE = COMMERCE_PARAM + 563
    const val PRODUCT_ADVERTISE_ID = COMMERCE_PARAM + 675
    const val PRODUCT_SOLD_OUT = COMMERCE_PARAM + 760
    const val PAYMENT_METHOD = COMMERCE_PARAM + 761
    const val SHIPPING_SERVICE_PROVIDER = COMMERCE_PARAM + 762
    const val SHIPPING_SPEED = COMMERCE_PARAM + 763
    const val SHIPPING_COST = COMMERCE_PARAM + 764
    const val MARK_UP = COMMERCE_PARAM + 765
    const val ORDER_STATUS = COMMERCE_PARAM + 766
    const val PRODUCT_VARIANT = COMMERCE_PARAM + 767
    const val PRODUCT_CURRENCY = "cr"
    const val STATUS_OF_SHOPPING_CARD = "st"
    const val ORDER_ID = "oi"
    const val ORDER_VALUE = "ov"
    const val PRODUCT_CATEGORY = "ca"
    const val PRODUCT_NAME = "ba"
    const val PRODUCT_COST = "co"
    const val PRODUCT_QUANTITY = "qn"
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
object PageParam {
    const val INTERNAL_SEARCH = "is"
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
object EventParam

@RestrictTo(RestrictTo.Scope.LIBRARY)
object SessionParam

@RestrictTo(RestrictTo.Scope.LIBRARY)
object CampaignParam {
    const val CAMPAIGN_PARAM = "cc"
    const val CAMPAIGN_ACTION_PARAM = "mca"
    const val MEDIA_CODE = "mc"
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
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
    /**
     * This custom param is used for event tracking only.
     *
     * Using it in page tracking, its value will be ignored in the analytics.
     */
    EVENT_PARAM("ck"),

    /**
     * Those custom params can be used for both page and event tracking.
     */
    ECOMMERCE_PARAM("cb"),
    PRODUCT_CATEGORY("ca"),
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
