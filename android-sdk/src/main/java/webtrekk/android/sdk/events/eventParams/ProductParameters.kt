package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.ECommerceParam

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class ProductParameters @JvmOverloads
constructor(
    var name: String = ""
) : BaseEvent {
    var categories: Map<Int, String> = emptyMap()
    var cost: Number? = null
    var quantity: Number? = null
    var productAdvertiseID: Number? = null
    var productSoldOut: Boolean? = null
    var productVariant: String? = null
    var ecommerceParameters: Map<Int, String> = emptyMap()
    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        categories.forEach { (key, value) ->
            map["${ECommerceParam.PRODUCT_CATEGORY}$key"] = value
        }
        return map
    }
}