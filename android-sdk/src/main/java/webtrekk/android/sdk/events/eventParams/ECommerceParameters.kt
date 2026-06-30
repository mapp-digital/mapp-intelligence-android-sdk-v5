package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.BaseParam
import webtrekk.android.sdk.ECommerceParam
import webtrekk.android.sdk.extension.addNotNull
import webtrekk.android.sdk.extension.formatNumber

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */

data class ECommerceParameters
@JvmOverloads
constructor(
    var customParameters: Map<Int, String> = emptyMap()
) : BaseEvent {
    enum class Status(val value: String) {
        NONE_STATUS(""),
        ADDED_TO_BASKET("add"),
        PURCHASED("conf"),
        VIEWED("view"),
        DELETED_FROM_BASKET("del"),
        ADDED_TO_WISHLIST("add-wl"),
        DELETED_FROM_WISHLIST("del-wl"),
        CHECKOUT("checkout");
    }

    var products = listOf<ProductParameters>()
    var status: Status = Status.NONE_STATUS
    var currency: String? = null
    var orderID: String? = null
    var orderValue: Number? = null
    var returningOrNewCustomer: String? = null
    var returnValue: Number? = null
    var cancellationValue: Number? = null
    var couponValue: Number? = null
    var paymentMethod: String? = null
    var shippingServiceProvider: String? = null
    var shippingSpeed: String? = null
    var shippingCost: Number? = null
    var markUp: Number? = null
    var orderStatus: String? = null
    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        customParameters.forEach { (key, value) -> map["${BaseParam.COMMERCE_PARAM}$key"] = value }
        addOrderFields(map)
        if (products.isNotEmpty()) addProductFields(map)
        return map
    }

    private fun addOrderFields(map: MutableMap<String, String>) {
        map.addNotNull(ECommerceParam.PRODUCT_CURRENCY, currency)
        map.addNotNull(ECommerceParam.ORDER_ID, orderID)
        map.addNotNull(ECommerceParam.ORDER_VALUE, orderValue)
        map.addNotNull(ECommerceParam.STATUS_OF_SHOPPING_CARD, status.value)
        map.addNotNull(ECommerceParam.RETURNING_OR_NEW_CUSTOMER, returningOrNewCustomer)
        map.addNotNull(ECommerceParam.RETURN_VALUE, returnValue)
        map.addNotNull(ECommerceParam.CANCELLATION_VALUE, cancellationValue)
        map.addNotNull(ECommerceParam.COUPON_VALUE, couponValue)
        map.addNotNull(ECommerceParam.PAYMENT_METHOD, paymentMethod)
        map.addNotNull(ECommerceParam.SHIPPING_SERVICE_PROVIDER, shippingServiceProvider)
        map.addNotNull(ECommerceParam.SHIPPING_SPEED, shippingSpeed)
        map.addNotNull(ECommerceParam.SHIPPING_COST, shippingCost)
        map.addNotNull(ECommerceParam.MARK_UP, markUp)
        map.addNotNull(ECommerceParam.ORDER_STATUS, orderStatus)
    }

    private fun addProductFields(map: MutableMap<String, String>) {
        val names = mutableListOf<String>()
        val costs = mutableListOf<String>()
        val quantities = mutableListOf<String>()
        val advertiseIDs = mutableListOf<String>()
        val soldOuts = mutableListOf<String>()
        val variants = mutableListOf<String>()
        val categoryKeys = mutableListOf<Int>()
        val ecomParamKeys = mutableListOf<Int>()
        var costsEmpty = true; var quantitiesEmpty = true
        var advertiseIDEmpty = true; var soldOutEmpty = true; var variantEmpty = true

        products.forEach { product ->
            names.add(product.name)
            costs.add(product.cost?.also { costsEmpty = false }?.formatNumber() ?: "")
            quantities.add(product.quantity?.also { quantitiesEmpty = false }?.formatNumber() ?: "")
            advertiseIDs.add(product.productAdvertiseID?.also { advertiseIDEmpty = false }?.formatNumber() ?: "")
            soldOuts.add(product.productSoldOut?.also { soldOutEmpty = false }?.let { if (it) "1" else "0" } ?: "")
            variants.add(product.productVariant?.also { variantEmpty = false } ?: "")
            product.categories.keys.filter { it !in categoryKeys }.forEach { categoryKeys.add(it) }
            product.ecommerceParameters.keys.filter { it !in ecomParamKeys }.forEach { ecomParamKeys.add(it) }
        }

        ecomParamKeys.forEach { key ->
            map.addNotNull("${BaseParam.COMMERCE_PARAM}$key",
                products.mapNotNull { it.ecommerceParameters[key] }.joinToString(";"))
        }
        categoryKeys.forEach { key ->
            map.addNotNull("${ECommerceParam.PRODUCT_CATEGORY}$key",
                products.mapNotNull { it.categories[key] }.joinToString(";"))
        }

        map.addNotNull(ECommerceParam.PRODUCT_NAME, names.joinToString(";"))
        if (!costsEmpty) map.addNotNull(ECommerceParam.PRODUCT_COST, costs.joinToString(";"))
        if (!quantitiesEmpty && status != Status.VIEWED)
            map.addNotNull(ECommerceParam.PRODUCT_QUANTITY, quantities.joinToString(";"))
        if (!advertiseIDEmpty) map.addNotNull(ECommerceParam.PRODUCT_ADVERTISE_ID, advertiseIDs.joinToString(";"))
        if (!soldOutEmpty) map.addNotNull(ECommerceParam.PRODUCT_SOLD_OUT, soldOuts.joinToString(";"))
        if (!variantEmpty) map.addNotNull(ECommerceParam.PRODUCT_VARIANT, variants.joinToString(";"))
    }
}