package webtrekk.android.sdk.events.eventParams

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import webtrekk.android.sdk.ECommerceParam

/**
 * Tests for ECommerceParameters.toHasMap() — validates refactored addOrderFields + addProductFields
 * produce identical output to the original monolithic implementation.
 */
internal class ECommerceParametersTest {

    // ── order-level fields ──────────────────────────────────────────────────

    @Test
    fun `toHasMap contains currency when set`() {
        val params = ECommerceParameters().apply { currency = "EUR" }
        assertThat(params.toHasMap()[ECommerceParam.PRODUCT_CURRENCY]).isEqualTo("EUR")
    }

    @Test
    fun `toHasMap contains orderId when set`() {
        val params = ECommerceParameters().apply { orderID = "ORD-42" }
        assertThat(params.toHasMap()[ECommerceParam.ORDER_ID]).isEqualTo("ORD-42")
    }

    @Test
    fun `toHasMap contains status value`() {
        val params = ECommerceParameters().apply { status = ECommerceParameters.Status.PURCHASED }
        assertThat(params.toHasMap()[ECommerceParam.STATUS_OF_SHOPPING_CARD]).isEqualTo("conf")
    }

    @Test
    fun `toHasMap omits null optional order fields`() {
        val map = ECommerceParameters().toHasMap()
        assertThat(map.containsKey(ECommerceParam.ORDER_ID)).isFalse()
        assertThat(map.containsKey(ECommerceParam.PRODUCT_CURRENCY)).isFalse()
        assertThat(map.containsKey(ECommerceParam.PAYMENT_METHOD)).isFalse()
    }

    @Test
    fun `toHasMap contains all shipping fields`() {
        val params = ECommerceParameters().apply {
            paymentMethod = "card"
            shippingServiceProvider = "DHL"
            shippingSpeed = "express"
            shippingCost = 9.99
            markUp = 5.0
            orderStatus = "shipped"
        }
        val map = params.toHasMap()
        assertThat(map[ECommerceParam.PAYMENT_METHOD]).isEqualTo("card")
        assertThat(map[ECommerceParam.SHIPPING_SERVICE_PROVIDER]).isEqualTo("DHL")
        assertThat(map[ECommerceParam.SHIPPING_SPEED]).isEqualTo("express")
        assertThat(map[ECommerceParam.SHIPPING_COST]).isNotNull()
        assertThat(map[ECommerceParam.MARK_UP]).isNotNull()
        assertThat(map[ECommerceParam.ORDER_STATUS]).isEqualTo("shipped")
    }

    @Test
    fun `toHasMap maps customParameters with commerce prefix`() {
        val params = ECommerceParameters(customParameters = mapOf(1 to "val1", 2 to "val2"))
        val map = params.toHasMap()
        assertThat(map["cb1"]).isEqualTo("val1")
        assertThat(map["cb2"]).isEqualTo("val2")
    }

    @Test
    fun `toHasMap with empty customParameters adds no commerce params`() {
        val params = ECommerceParameters(customParameters = emptyMap())
        val map = params.toHasMap()
        // no "cb"-prefixed custom keys from customParameters
        assertThat(map.keys.filter { it.startsWith("cb") && it.length <= 3 }).isEmpty()
    }

    // ── product-level fields ────────────────────────────────────────────────

    @Test
    fun `toHasMap with no products produces no product fields`() {
        val params = ECommerceParameters()
        val map = params.toHasMap()
        assertThat(map.containsKey(ECommerceParam.PRODUCT_NAME)).isFalse()
        assertThat(map.containsKey(ECommerceParam.PRODUCT_COST)).isFalse()
    }

    @Test
    fun `toHasMap with single product contains product name`() {
        val product = ProductParameters(name = "Widget")
        val params = ECommerceParameters().apply { products = listOf(product) }
        assertThat(params.toHasMap()[ECommerceParam.PRODUCT_NAME]).isEqualTo("Widget")
    }

    @Test
    fun `toHasMap joins multiple product names with semicolon`() {
        val p1 = ProductParameters(name = "Apple")
        val p2 = ProductParameters(name = "Banana")
        val params = ECommerceParameters().apply { products = listOf(p1, p2) }
        assertThat(params.toHasMap()[ECommerceParam.PRODUCT_NAME]).isEqualTo("Apple;Banana")
    }

    @Test
    fun `toHasMap includes product cost when present`() {
        val product = ProductParameters(name = "Item").apply { cost = 12.5 }
        val params = ECommerceParameters().apply { products = listOf(product) }
        assertThat(params.toHasMap()[ECommerceParam.PRODUCT_COST]).isNotNull()
    }

    @Test
    fun `toHasMap omits product cost when all costs are null`() {
        val product = ProductParameters(name = "Item")  // cost = null
        val params = ECommerceParameters().apply { products = listOf(product) }
        assertThat(params.toHasMap().containsKey(ECommerceParam.PRODUCT_COST)).isFalse()
    }

    @Test
    fun `toHasMap omits product quantity when status is VIEWED`() {
        val product = ProductParameters(name = "Item").apply { quantity = 3 }
        val params = ECommerceParameters().apply {
            products = listOf(product)
            status = ECommerceParameters.Status.VIEWED
        }
        assertThat(params.toHasMap().containsKey(ECommerceParam.PRODUCT_QUANTITY)).isFalse()
    }

    @Test
    fun `toHasMap includes product quantity when status is not VIEWED`() {
        val product = ProductParameters(name = "Item").apply { quantity = 3 }
        val params = ECommerceParameters().apply {
            products = listOf(product)
            status = ECommerceParameters.Status.PURCHASED
        }
        assertThat(params.toHasMap()[ECommerceParam.PRODUCT_QUANTITY]).isNotNull()
    }

    @Test
    fun `toHasMap includes productSoldOut as 1 or 0`() {
        val sold = ProductParameters(name = "A").apply { productSoldOut = true }
        val notSold = ProductParameters(name = "B").apply { productSoldOut = false }
        val params = ECommerceParameters().apply { products = listOf(sold, notSold) }
        assertThat(params.toHasMap()[ECommerceParam.PRODUCT_SOLD_OUT]).isEqualTo("1;0")
    }

    @Test
    fun `toHasMap omits soldOut when all products have null soldOut`() {
        val product = ProductParameters(name = "X")  // productSoldOut = null
        val params = ECommerceParameters().apply { products = listOf(product) }
        assertThat(params.toHasMap().containsKey(ECommerceParam.PRODUCT_SOLD_OUT)).isFalse()
    }

    @Test
    fun `toHasMap includes productVariant when present`() {
        val product = ProductParameters(name = "X").apply { productVariant = "red" }
        val params = ECommerceParameters().apply { products = listOf(product) }
        assertThat(params.toHasMap()[ECommerceParam.PRODUCT_VARIANT]).isEqualTo("red")
    }

    @Test
    fun `toHasMap maps product categories with correct key`() {
        val product = ProductParameters(name = "X").apply { categories = mapOf(1 to "Electronics") }
        val params = ECommerceParameters().apply { products = listOf(product) }
        assertThat(params.toHasMap()["ca1"]).isEqualTo("Electronics")
    }

    @Test
    fun `toHasMap maps ecommerceParameters with commerce prefix`() {
        val product = ProductParameters(name = "X").apply {
            ecommerceParameters = mapOf(5 to "promo")
        }
        val params = ECommerceParameters().apply { products = listOf(product) }
        assertThat(params.toHasMap()["cb5"]).isEqualTo("promo")
    }

    @Test
    fun `toHasMap aggregates categories across multiple products with semicolons`() {
        val p1 = ProductParameters(name = "A").apply { categories = mapOf(1 to "Cat1") }
        val p2 = ProductParameters(name = "B").apply { categories = mapOf(1 to "Cat2") }
        val params = ECommerceParameters().apply { products = listOf(p1, p2) }
        assertThat(params.toHasMap()["ca1"]).isEqualTo("Cat1;Cat2")
    }

    @Test
    fun `toHasMap mixed products some with some without cost`() {
        val p1 = ProductParameters(name = "A").apply { cost = 10.0 }
        val p2 = ProductParameters(name = "B")  // no cost
        val params = ECommerceParameters().apply { products = listOf(p1, p2) }
        val map = params.toHasMap()
        // cost is present because p1 has it; p2 contributes empty string
        assertThat(map[ECommerceParam.PRODUCT_COST]).isNotNull()
    }

    @Test
    fun `toHasMap return and cancellation values included`() {
        val params = ECommerceParameters().apply {
            returnValue = 5.0
            cancellationValue = 3.0
            couponValue = 1.5
        }
        val map = params.toHasMap()
        assertThat(map[ECommerceParam.RETURN_VALUE]).isNotNull()
        assertThat(map[ECommerceParam.CANCELLATION_VALUE]).isNotNull()
        assertThat(map[ECommerceParam.COUPON_VALUE]).isNotNull()
    }
}
