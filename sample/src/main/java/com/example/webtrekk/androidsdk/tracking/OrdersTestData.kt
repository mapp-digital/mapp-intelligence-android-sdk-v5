package com.example.webtrekk.androidsdk.tracking

import java.util.*
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.events.PageViewEvent
import webtrekk.android.sdk.events.eventParams.ECommerceParameters
import webtrekk.android.sdk.events.eventParams.ProductParameters
import kotlin.random.Random

class OrdersTestData {
    fun createProducts(): List<ProductParameters> {
        val products = mutableListOf<ProductParameters>()
        for (i in 0 until Random.nextInt(10)) {
            val product = ProductParameters()
            product.name = "Product-${i}"
            product.categories = mapOf(Pair(1, "ProductCat1"), Pair(2, "ProductCat2"))
            product.cost = Random.nextInt(100)
            product.quantity = Random.nextInt(10)
            products.add(product)
        }
        return products
    }

    fun createOrder() {
        val products = createProducts()
        val ecommerceParameters = ECommerceParameters()
        ecommerceParameters.products = products
        ecommerceParameters.currency = "EUR"
        ecommerceParameters.orderID = UUID.randomUUID().toString().slice(IntRange(0, 6))
        ecommerceParameters.paymentMethod = "Credit Card"
        ecommerceParameters.shippingServiceProvider = "DHL"
        ecommerceParameters.shippingSpeed = "express"
        ecommerceParameters.shippingCost = Random.nextInt(40)
        ecommerceParameters.couponValue = Random.nextInt(20)
        ecommerceParameters.orderValue = ecommerceParameters.calculateOrderValue()
        ecommerceParameters.status = ECommerceParameters.Status.PURCHASED
        ecommerceParameters.markUp = 1
        ecommerceParameters.orderStatus = "order received"
        ecommerceParameters.returningOrNewCustomer = "new customer"

        val pageEvent = PageViewEvent(name = "TestOrderTracking")
        pageEvent.eCommerceParameters = ecommerceParameters

        Webtrekk.getInstance().trackPage(pageEvent)
    }

    private fun ECommerceParameters.calculateOrderValue(): Number {
        var totalCost = 0.0
        this.products.forEach { product ->
            totalCost += (if (product.cost == null) 0.0 else product.cost!!.toDouble()) * if (product.quantity != null) product.quantity!!.toDouble() else 1.0
        }
        totalCost += (if (this.shippingCost == null) 0.0 else this.shippingCost!!.toDouble()) - if (this.couponValue != null) this.couponValue!!.toDouble() else 0.0
        return totalCost
    }
}