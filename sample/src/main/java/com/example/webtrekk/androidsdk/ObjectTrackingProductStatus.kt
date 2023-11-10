package com.example.webtrekk.androidsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_object_tracking_product_status.btnAddedToBasket
import kotlinx.android.synthetic.main.activity_object_tracking_product_status.btnAddedToWishlist
import kotlinx.android.synthetic.main.activity_object_tracking_product_status.btnCheckout
import kotlinx.android.synthetic.main.activity_object_tracking_product_status.btnDeletedFromBasket
import kotlinx.android.synthetic.main.activity_object_tracking_product_status.btnDeletedFromWishlist
import kotlinx.android.synthetic.main.activity_object_tracking_product_status.btnNoneStatus
import kotlinx.android.synthetic.main.activity_object_tracking_product_status.btnPurchaseProduct
import kotlinx.android.synthetic.main.activity_object_tracking_product_status.btnViewProduct
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.events.PageViewEvent
import webtrekk.android.sdk.events.eventParams.ECommerceParameters
import webtrekk.android.sdk.events.eventParams.ProductParameters

class ObjectTrackingProductStatus : AppCompatActivity() {

    private val product1: ProductParameters = run {
        val product = ProductParameters()
        product.name = "Product1"
        product.categories = mapOf(Pair(1, "ProductCat1"), Pair(2, "ProductCat2"))
        product.cost = 13
        product.quantity = 4
        return@run product
    }

    private val product2: ProductParameters = run {
        val product = ProductParameters()
        product.name = "Product2"
        product.categories = mapOf(Pair(2, "ProductCat2"))
        product.cost = 50
        return@run product
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_tracking_product_status)

        btnNoneStatus.setOnClickListener {
            trackEcommerceNoneStatus()
        }

        btnPurchaseProduct.setOnClickListener {
            trackEcommercePurchaseProduct()
        }

        btnViewProduct.setOnClickListener {
            trackEcommerceViewProduct()
        }

        btnAddedToBasket.setOnClickListener {
            trackEcommerceAddedToBasket()
        }

        btnDeletedFromBasket.setOnClickListener {
            trackEcommerceDeletedFromBasket()
        }

        btnAddedToWishlist.setOnClickListener {
            trackEcommerceAddedToWishlist()
        }

        btnDeletedFromWishlist.setOnClickListener {
            trackEcommerceDeletedFromWishlist()
        }

        btnCheckout.setOnClickListener {
            trackEcommerceCheckout()
        }
    }

    private fun trackEcommerceNoneStatus() {

        val ecommerceParameters1 = ECommerceParameters(
            customParameters = mapOf(
                1 to "ProductParam1",
                2 to "ProductParam2"
            )
        )
        ecommerceParameters1.products = listOf(product1)
        ecommerceParameters1.status = ECommerceParameters.Status.NONE_STATUS
        ecommerceParameters1.cancellationValue = 2
        ecommerceParameters1.couponValue = 33
        ecommerceParameters1.currency = "EUR"
        ecommerceParameters1.markUp = 1
        ecommerceParameters1.orderStatus = "order received"
        ecommerceParameters1.orderID = "ud679adn"
        ecommerceParameters1.orderValue = 456
        ecommerceParameters1.paymentMethod = "credit card"
        ecommerceParameters1.returnValue = 3
        ecommerceParameters1.returningOrNewCustomer = "new customer"
        ecommerceParameters1.shippingCost = 35
        ecommerceParameters1.shippingSpeed = "highest"
        ecommerceParameters1.shippingServiceProvider = "DHL"

        val pageEvent = PageViewEvent(name = "TrackProductNoneStatus")
        ecommerceParameters1.products = listOf(product1,product2)
        pageEvent.eCommerceParameters = ecommerceParameters1
        Webtrekk.getInstance().trackPage(pageEvent)
    }

    private fun trackEcommercePurchaseProduct() {

        val ecommerceParameters1 = ECommerceParameters(
            customParameters = mapOf(
                1 to "ProductParam1",
                2 to "ProductParam2"
            )
        )
        ecommerceParameters1.products = listOf(product1)
        ecommerceParameters1.status = ECommerceParameters.Status.PURCHASED
        ecommerceParameters1.cancellationValue = 2
        ecommerceParameters1.couponValue = 33
        ecommerceParameters1.currency = "EUR"
        ecommerceParameters1.markUp = 1
        ecommerceParameters1.orderStatus = "order received"
        ecommerceParameters1.orderID = "ud679adn"
        ecommerceParameters1.orderValue = 456
        ecommerceParameters1.paymentMethod = "credit card"
        ecommerceParameters1.returnValue = 3
        ecommerceParameters1.returningOrNewCustomer = "new customer"
        ecommerceParameters1.shippingCost = 35
        ecommerceParameters1.shippingSpeed = "highest"
        ecommerceParameters1.shippingServiceProvider = "DHL"

        val pageEvent = PageViewEvent(name = "TrackProductPurchased")
        ecommerceParameters1.products = listOf(product1,product2)
        pageEvent.eCommerceParameters = ecommerceParameters1
        Webtrekk.getInstance().trackPage(pageEvent)
    }

    private fun trackEcommerceViewProduct() {

        val ecommerceParameters1 = ECommerceParameters(
            customParameters = mapOf(
                1 to "ProductParam1",
                2 to "ProductParam2"
            )
        )
        ecommerceParameters1.products = listOf(product1)
        ecommerceParameters1.status = ECommerceParameters.Status.VIEWED
        ecommerceParameters1.cancellationValue = 2
        ecommerceParameters1.couponValue = 33
        ecommerceParameters1.currency = "EUR"
        ecommerceParameters1.markUp = 1
        ecommerceParameters1.orderStatus = "order received"
        ecommerceParameters1.orderID = "ud679adn"
        ecommerceParameters1.orderValue = 456
        ecommerceParameters1.paymentMethod = "credit card"
        ecommerceParameters1.returnValue = 3
        ecommerceParameters1.returningOrNewCustomer = "new customer"
        ecommerceParameters1.shippingCost = 35
        ecommerceParameters1.shippingSpeed = "highest"
        ecommerceParameters1.shippingServiceProvider = "DHL"

        val pageEvent = PageViewEvent(name = "TrackProductView")
        ecommerceParameters1.products = listOf(product1,product2)
        pageEvent.eCommerceParameters = ecommerceParameters1
        Webtrekk.getInstance().trackPage(pageEvent)
    }

    private fun trackEcommerceAddedToBasket() {

        val ecommerceParameters1 = ECommerceParameters(
            customParameters = mapOf(
                1 to "ProductParam1",
                2 to "ProductParam2"
            )
        )
        product1.quantity = 3
        product2.quantity = 2

        val pageEvent = PageViewEvent(name = "TrackProductAddedToBasket")
        ecommerceParameters1.status = ECommerceParameters.Status.ADDED_TO_BASKET
        ecommerceParameters1.products = listOf(product1,product2)
        pageEvent.eCommerceParameters = ecommerceParameters1
        Webtrekk.getInstance().trackPage(pageEvent)
    }

    private fun trackEcommerceDeletedFromBasket() {

        val ecommerceParameters1 = ECommerceParameters(
            customParameters = mapOf(
                1 to "ProductParam1",
                2 to "ProductParam2"
            )
        )
        product1.quantity = 3
        product2.quantity = 2

        val pageEvent = PageViewEvent(name = "TrackProductDeletedFromBasket")
        ecommerceParameters1.status = ECommerceParameters.Status.DELETED_FROM_BASKET
        ecommerceParameters1.products = listOf(product1,product2)
        pageEvent.eCommerceParameters = ecommerceParameters1
        Webtrekk.getInstance().trackPage(pageEvent)
    }

    private fun trackEcommerceAddedToWishlist() {

        val ecommerceParameters1 = ECommerceParameters(
            customParameters = mapOf(
                1 to "ProductParam1",
                2 to "ProductParam2"
            )
        )
        product1.quantity = 3
        product2.quantity = 2

        val pageEvent = PageViewEvent(name = "TrackProductAddedToWishlist")
        ecommerceParameters1.status = ECommerceParameters.Status.ADDED_TO_WISHLIST
        ecommerceParameters1.products = listOf(product1,product2)
        pageEvent.eCommerceParameters = ecommerceParameters1
        Webtrekk.getInstance().trackPage(pageEvent)
    }

    private fun trackEcommerceDeletedFromWishlist() {

        val ecommerceParameters1 = ECommerceParameters(
            customParameters = mapOf(
                1 to "ProductParam1",
                2 to "ProductParam2"
            )
        )
        product1.quantity = 3
        product2.quantity = 2

        val pageEvent = PageViewEvent(name = "TrackProductDeletedFromWishlist")
        ecommerceParameters1.status = ECommerceParameters.Status.DELETED_FROM_WISHLIST
        ecommerceParameters1.products = listOf(product1,product2)
        pageEvent.eCommerceParameters = ecommerceParameters1
        Webtrekk.getInstance().trackPage(pageEvent)
    }

    private fun trackEcommerceCheckout() {

        val ecommerceParameters1 = ECommerceParameters(
            customParameters = mapOf(
                1 to "ProductParam1",
                2 to "ProductParam2"
            )
        )
        product1.quantity = 3
        product2.quantity = 2

        val pageEvent = PageViewEvent(name = "TrackProductCheckout")
        ecommerceParameters1.status = ECommerceParameters.Status.CHECKOUT
        ecommerceParameters1.products = listOf(product1,product2)
        pageEvent.eCommerceParameters = ecommerceParameters1
        Webtrekk.getInstance().trackPage(pageEvent)
    }
}