package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class ECommerceParameters(
    var customParameters: Map<Int, String> = emptyMap()
) {
    enum class Status {
        noneStatus,
        addedToBasket,
        purchased,
        viewed
    }

    var products = listOf<ProductParameters>()
    var status: Status? = null
    var orderID: String = ""
    var orderValue: String = ""
    var returningOrNewCustomer: String = ""
    var returnValue: String = ""
    var cancellationValue: String = ""
    var couponValue: String = ""
    var productAdvertiseID: String = ""
    var productSoldOut: String = ""
    var paymentMethod: String = ""
    var shippingServiceProvider: String = ""
    var shippingSpeed: String = ""
    var shippingCost: String = ""
    var markUp: String = ""
    var orderStatus: String = ""
    var productVariant: String = ""
}