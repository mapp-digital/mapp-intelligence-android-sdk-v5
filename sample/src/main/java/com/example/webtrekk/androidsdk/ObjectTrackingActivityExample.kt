package com.example.webtrekk.androidsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_object_tracking.button_test_anonymous_tracking
import kotlinx.android.synthetic.main.activity_object_tracking.example10
import kotlinx.android.synthetic.main.activity_object_tracking.example3
import kotlinx.android.synthetic.main.activity_object_tracking.example4
import kotlinx.android.synthetic.main.activity_object_tracking.example5
import kotlinx.android.synthetic.main.activity_object_tracking.example6
import kotlinx.android.synthetic.main.activity_object_tracking.example7
import kotlinx.android.synthetic.main.activity_object_tracking.example8
import kotlinx.android.synthetic.main.activity_object_tracking.example9
import kotlinx.android.synthetic.main.activity_object_tracking.firstExample
import kotlinx.android.synthetic.main.activity_object_tracking.secondExample
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
        example9.setOnClickListener {
            ecommerceTracking()
        }

        example10.setOnClickListener {
            ecommerceTrackingTest2()
        }

        button_test_anonymous_tracking.setOnClickListener {
            testAnonymousTracking()
        }

    }

    private fun testAnonymousTracking() {
        val trackingParams = mapOf("uc709" to "Nis, Cicevac", "uc703" to "Stefan")
        Webtrekk.getInstance().trackCustomPage(
            pageName = "customName",
            trackingParams = trackingParams
        )
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
        //userCategories.emailReceiverId="111111111"

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


    private val product3: ProductParameters = run {
        val product = ProductParameters()
        product.name = "ABC-123"
        product.categories = mapOf(Pair(1, "tops"), Pair(2, "noname"))
        product.ecommerceParameters = mapOf(Pair(1, "product1 param 1"))
        product.cost = 99.90
        product.quantity = 2
        product.productSoldOut = false
        product.productVariant = "green"
        return@run product
    }

    private val product4: ProductParameters = run {
        val product = ProductParameters()
        product.name = "ABC-456"
        product.cost = 33.33
        product.quantity = 2
        product.productSoldOut = false
        product.productAdvertiseID = 562918888888888.2223;
        product.productVariant = "blue"
        product.categories = mapOf(Pair(1, "t-shirt"), Pair(2, "gucci"))
        product.ecommerceParameters = mapOf(Pair(1, "product2 param 1"))
        return@run product
    }

    private val product5: ProductParameters = run {
        val product = ProductParameters()
        product.name = "TESTTEASASS"
        product.cost = 553
        product.quantity = 4333333
        product.productSoldOut = true
        product.productVariant = "sisarka"
        product.productAdvertiseID = 23212312312
        product.categories = mapOf(Pair(1, "t-shirtsss"), Pair(2, "gucciss"))
        product.ecommerceParameters = mapOf(Pair(1, "product3 param 1"))
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


    private fun ecommerceTracking() {
        val ecommerceParameters1 = ECommerceParameters()
        ecommerceParameters1.customParameters = mapOf(2 to "goal param 2")

        val pageEvent1 = PageViewEvent("pageName")
        pageEvent1.pageParameters = PageParameters(
            parameters = mapOf(
                5 to "parameter value 5",
                777 to "this is my page type"
            )
        )
        pageEvent1.eCommerceParameters = ecommerceParameters1
        ecommerceParameters1.products = listOf(product3, product4)
        ecommerceParameters1.status = ECommerceParameters.Status.ADDED_TO_BASKET
        Webtrekk.getInstance().trackPage(pageEvent1)
    }


    private fun ecommerceTrackingTest2() {
        val ecommerceParameters1 = ECommerceParameters()
        ecommerceParameters1.customParameters = mapOf(44 to "goal param 44")

        val pageEvent1 = PageViewEvent("pageName")
        pageEvent1.pageParameters = PageParameters(
            parameters = mapOf(
                5 to "parameter value 123",
                777 to "this is my page fasda"
            )
        )
        pageEvent1.eCommerceParameters = ecommerceParameters1
        ecommerceParameters1.products = listOf(product3, product5)
        ecommerceParameters1.status = ECommerceParameters.Status.PURCHASED
        Webtrekk.getInstance().trackPage(pageEvent1)
    }

}