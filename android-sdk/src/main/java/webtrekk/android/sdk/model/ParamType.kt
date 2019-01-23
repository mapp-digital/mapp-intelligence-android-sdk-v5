package webtrekk.android.sdk.model

import androidx.annotation.IntRange

object Param {

    // Page Data PAGE ONLY
    const val INTERNAL_SEARCH = "is"

    // Campaign Data BOTH
    const val MEDIA_CODE = "mc"

    // User Data BOTH
    const val CUSTOMER_ID = "cd"

    // E-Commerce Data PAGE ONLY
    const val PRODUCT_NAME = "ba"
    const val PRODUCT_COST = "co"
    const val PRODUCT_CURRENCY = "cr"
    const val PRODUCT_QUANTITY = "qn"
    const val STATUS_OF_SHOPPING_CARD = "st"
    const val ORDER_ID = "oi"
    const val ORDER_VALUE = "ov"
}

enum class ParamType(val value: String) {
    PAGE_PARAM("cp"), // page only
    PAGE_CATEGORY("cg"), // page only
    ACTION_PARAM("ck"), // event only
    CAMPAIGN_PARAM("cc"), // both
    SESSION_PARAM("cs"), // both
    URM_CATEGORY("uc"), // both
    ECOMMERCE_PARAM("cb"), // page only
    PRODUCT_CATEGORY("ca") // page only
}

typealias TrackingParams = LinkedHashMap<String, String>

@JvmName("createCustomParam")
fun customParam(paramType: ParamType, @IntRange(from = 0, to = 500) value: Int): String =
    "${paramType.value}$value"

// get restrictive on the params either Page or Event
internal enum class RequestType(val value: String) {

    PAGE(""),
    EVENT("ct")
}
