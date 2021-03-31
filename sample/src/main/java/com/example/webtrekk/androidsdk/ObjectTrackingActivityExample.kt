package com.example.webtrekk.androidsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_object_tracking.*
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.events.ActionEvent
import webtrekk.android.sdk.events.MediaEvent
import webtrekk.android.sdk.events.PageViewEvent
import webtrekk.android.sdk.events.eventParams.ECommerceParameters
import webtrekk.android.sdk.events.eventParams.EventParameters
import webtrekk.android.sdk.events.eventParams.MediaParameters
import webtrekk.android.sdk.events.eventParams.PageParameters
import webtrekk.android.sdk.events.eventParams.ProductParameters
import webtrekk.android.sdk.events.eventParams.SessionParameters
import webtrekk.android.sdk.events.eventParams.UserCategories

class ObjectTrackingActivityExample : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_tracking)


        firstExample.setOnClickListener {
            trackAction()
        }

        secondExample.setOnClickListener {
            trackCustomAction()
        }

        example3.setOnClickListener {
            trackGoal()
        }
        example4.setOnClickListener {
            trackEcommerceViewProduct()
        }
        example5.setOnClickListener {
            trackEcommerceAddedToBasket()
        }
        example6.setOnClickListener {
            trackEcommerceConfirmation()
        }
        example7.setOnClickListener {
            trackCustomPage()
        }

        example8.setOnClickListener {
            trackMedia1()
        }

    }


    private fun trackAction() {
        val eventParameters = EventParameters(mapOf(Pair(20, "ck20Param1")))

        val event = ActionEvent("TestAction")
        event.eventParameters = eventParameters

        Webtrekk.getInstance().trackAction(event)
    }

    private fun trackCustomAction() {
        val eventParameters = EventParameters(mapOf(Pair(20, "ck20Param1")))
        //user properties
        val userCategories = UserCategories()
        userCategories.customCategories = mapOf(Pair(20, "userParam1"))
        userCategories.birthday = UserCategories.Birthday(12, 1, 1993)
        userCategories.city = "Paris"
        userCategories.country = "France"
        userCategories.customerId = "CustomerID"
        userCategories.gender = UserCategories.Gender.FEMALE

        //sessionproperties
        val sessionParameters = SessionParameters(mapOf(Pair(10, "sessionParam1")))

        val event = ActionEvent("TestAction")
        event.eventParameters = eventParameters
        event.userCategories = userCategories
        event.sessionParameters = sessionParameters

        Webtrekk.getInstance().trackAction(event)
    }

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


    private val ecommerceParameters: ECommerceParameters = ECommerceParameters(
        mapOf(1 to "ProductParam1;ProductParam1", 2 to "ProductParam2")
    )


    private fun trackGoal() {
        val ecommerceParameters = ECommerceParameters(customParameters = mapOf(1 to "goal value 1"))
        val pageEvent = PageViewEvent(name = "page name")
        pageEvent.eCommerceParameters = ecommerceParameters

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
        ecommerceParameters1.status = ECommerceParameters.Status.PURCHASED
        ecommerceParameters1.cancellationValue = 2
        ecommerceParameters1.couponValue = 33
        ecommerceParameters1.currency = "EUR"
        ecommerceParameters1.markUp = 1
        ecommerceParameters1.orderStatus = "order received"
        ecommerceParameters1.orderID = "ud679adn"
        ecommerceParameters1.orderValue = 456
        ecommerceParameters1.paymentMethod = "credit card"
        ecommerceParameters1.productAdvertiseID = 56291
        ecommerceParameters1.productSoldOut = 1
        ecommerceParameters1.returnValue = 3
        ecommerceParameters1.returningOrNewCustomer = "new customer"
        ecommerceParameters1.shippingCost = 35
        ecommerceParameters1.shippingSpeed = "highest"
        ecommerceParameters1.shippingServiceProvider = "DHL"

        val pageEvent = PageViewEvent(name = "TrackProductView")
        pageEvent.eCommerceParameters = ecommerceParameters1

        Webtrekk.getInstance().trackPage(pageEvent)

        ecommerceParameters1.products = listOf(product2)
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

        ecommerceParameters1.status = ECommerceParameters.Status.ADDED_TO_BASKET
        ecommerceParameters1.products = listOf(product1)

        val pageEvent = PageViewEvent(name = "TrackProductAddedToBasket")
        pageEvent.eCommerceParameters = ecommerceParameters1


        Webtrekk.getInstance().trackPage(pageEvent)

        ecommerceParameters1.products = listOf(product2)

        Webtrekk.getInstance().trackPage(pageEvent)
    }

    private fun trackEcommerceConfirmation() {

        product1.quantity = 3
        product2.quantity = 2

        ecommerceParameters.products = listOf(product1, product2)
        ecommerceParameters.currency = "EUR"
        ecommerceParameters.orderID = "1234nb5"
        ecommerceParameters.paymentMethod = "Credit Card"
        ecommerceParameters.shippingServiceProvider = "DHL"
        ecommerceParameters.shippingSpeed = "express"
        ecommerceParameters.shippingCost = 20
        ecommerceParameters.couponValue = 10
        ecommerceParameters.orderValue = ecommerceParameters.calculateOrderValue()
        ecommerceParameters.status = ECommerceParameters.Status.PURCHASED

        val pageEvent = PageViewEvent(name = "TrackProductConfirmed")
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

    private fun trackCustomPage() {

        //page properties
        val params = mapOf(20 to "cp20Override")
        val categories = mapOf(10 to "test")
        val searchTerm = "testSearchTerm"
        val pageParameters =
            PageParameters(parameters = params, pageCategory = categories, search = searchTerm)

        //user properties
        val userCategories = UserCategories()
        userCategories.customCategories = mapOf(20 to "userParam1")
        userCategories.birthday = UserCategories.Birthday(day = 12, month = 1, year = 1993)
        userCategories.city = "Paris"
        userCategories.country = "France"
        userCategories.customerId = "CustomerID"
        userCategories.gender = UserCategories.Gender.FEMALE

        //sessionproperties
        val sessionParameters = SessionParameters(parameters = mapOf(10 to "sessionParam1"))

        val pageEvent = PageViewEvent(name = "the custom name of page")
        pageEvent.pageParameters = pageParameters
        pageEvent.userCategories = userCategories
        pageEvent.sessionParameters = sessionParameters

        Webtrekk.getInstance().trackPage(pageEvent)

    }

    private fun trackMedia1() {
        val mediaProperties =
            MediaParameters("TestVideo", action = "view", position = 12, duration = 120)
        mediaProperties.customCategories = mapOf(20 to "mediaCat")
        val mediaEvent = MediaEvent(pageName = "Test", parameters = mediaProperties)
        Webtrekk.getInstance().trackMedia(mediaEvent)
    }

}