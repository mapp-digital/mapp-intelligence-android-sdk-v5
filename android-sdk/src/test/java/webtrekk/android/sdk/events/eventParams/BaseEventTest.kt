package webtrekk.android.sdk.events.eventParams

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Tests for BaseEvent fun interface — validates that the SAM conversion works
 * and that the interface is still functional after the change.
 */
internal class BaseEventTest {

    @Test
    fun `BaseEvent fun interface can be implemented as lambda`() {
        val event: BaseEvent = BaseEvent { mutableMapOf("key" to "value") }
        assertThat(event.toHasMap()).containsEntry("key", "value")
    }

    @Test
    fun `BaseEvent fun interface lambda returns mutable map`() {
        val event: BaseEvent = BaseEvent { mutableMapOf("a" to "1", "b" to "2") }
        val map = event.toHasMap()
        map["c"] = "3"  // should not throw — it's mutable
        assertThat(map).hasSize(3)
    }

    @Test
    fun `BaseEvent anonymous class still works after fun interface change`() {
        val event = object : BaseEvent {
            override fun toHasMap() = mutableMapOf("x" to "y")
        }
        assertThat(event.toHasMap()).containsEntry("x", "y")
    }

    @Test
    fun `ECommerceParameters still implements BaseEvent`() {
        val params = ECommerceParameters()
        assertThat(params).isInstanceOf(BaseEvent::class.java)
        assertThat(params.toHasMap()).isNotNull()
    }

    @Test
    fun `MediaParameters still implements BaseEvent`() {
        val params = MediaParameters(
            name = "v",
            action = MediaParameters.Action.PLAY.code(),
            position = 0.0,
            duration = 10.0
        )
        assertThat(params).isInstanceOf(BaseEvent::class.java)
        assertThat(params.toHasMap()).isNotNull()
    }
}
