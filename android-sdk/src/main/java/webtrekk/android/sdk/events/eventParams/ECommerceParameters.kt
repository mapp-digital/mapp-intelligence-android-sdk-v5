package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.ECommerceParam
import webtrekk.android.sdk.extension.addNotNull

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class ECommerceParameters(
    var customParameters: Map<Int, String> = emptyMap()
) : BaseEvent {
    enum class Status {
        NONE_STATUS,
        ADDED_TO_BASKET,
        PURCHASED,
        VIEWED;

        fun toStatus(): String {
            return when (this) {
                ADDED_TO_BASKET -> "add"
                PURCHASED -> "conf"
                VIEWED -> "view"
                NONE_STATUS -> ""
            }
        }
    }

    // TODO Implement Later
    var products = listOf<ProductParameters>()
    var status: Status = Status.NONE_STATUS
    var currency: String? = null
    var orderID: String? = null
    var orderValue: String? = null
    var returningOrNewCustomer: String? = null
    var returnValue: String? = null
    var cancellationValue: String? = null
    var couponValue: String? = null
    var productAdvertiseID: String? = null
    var productSoldOut: String? = null
    var paymentMethod: String? = null
    var shippingServiceProvider: String? = null
    var shippingSpeed: String? = null
    var shippingCost: String? = null
    var markUp: String? = null
    var orderStatus: String? = null
    var productVariant: String? = null
    override suspend fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        if (!customParameters.isNullOrEmpty()) {
            customParameters.forEach { (key, value) ->
                map["${ECommerceParam.COMMERCE_PARAM}$key"] = value
            }
        }
        map.addNotNull(ECommerceParam.PRODUCT_CURRENCY, currency)
        map.addNotNull(ECommerceParam.ORDER_ID, orderID)
        map.addNotNull(ECommerceParam.ORDER_VALUE, orderValue)
        map.addNotNull(ECommerceParam.STATUS_OF_SHOPPING_CARD, status.toStatus())
        map.addNotNull(ECommerceParam.RETURNING_OR_NEW_CUSTOMER, returningOrNewCustomer)
        map.addNotNull(ECommerceParam.RETURN_VALUE, returnValue)
        map.addNotNull(ECommerceParam.CANCELLATION_VALUE, cancellationValue)
        map.addNotNull(ECommerceParam.COUPON_VALUE, couponValue)
        map.addNotNull(ECommerceParam.PRODUCT_ADVERTISE_ID, productAdvertiseID)
        map.addNotNull(ECommerceParam.PRODUCT_SOLD_OUT, productSoldOut)
        map.addNotNull(ECommerceParam.PAYMENT_METHOD, paymentMethod)
        map.addNotNull(ECommerceParam.SHIPPING_SERVICE_PROVIDER, shippingServiceProvider)
        map.addNotNull(ECommerceParam.SHIPPING_SPEED, shippingSpeed)
        map.addNotNull(ECommerceParam.SHIPPING_COST, shippingCost)
        map.addNotNull(ECommerceParam.MARK_UP, markUp)
        map.addNotNull(ECommerceParam.ORDER_STATUS, orderStatus)
        map.addNotNull(ECommerceParam.PRODUCT_VARIANT, productVariant)

        return map
    }
}