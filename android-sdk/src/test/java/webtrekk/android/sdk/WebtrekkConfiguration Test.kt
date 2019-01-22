package webtrekk.android.sdk

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class `WebtrekkConfiguration Test` {

    private lateinit var webtrekkConfiguration: WebtrekkConfiguration

    @Before
    fun setUp() {
        webtrekkConfiguration =
            WebtrekkConfiguration.Builder(listOf("123456789", "123"), "www.webtrekk.com")
                .sendDelay(sendDelay = 20)
                .disableAutoTracking()
                .build()
    }

    @Test
    fun `validate trackIds are not null nor empty nor containing any null or empty values`() {
        webtrekkConfiguration.trackIds
    }

    @Test
    fun `validate trackDomain is not null nor blank`() {
        webtrekkConfiguration.trackDomain
    }

    @Test
    fun `validate default values`() {
        val defaultWebtrekkConfiguration =
            WebtrekkConfiguration.Builder(listOf("123"), "www.webtrekk.com").build()

        assertEquals(LOG_LEVEL_DEFAULT, defaultWebtrekkConfiguration.logLevel)
        assertEquals(
            TIME_UNIT_DEFAULT.toMillis(SEND_DELAY_DEFAULT),
            defaultWebtrekkConfiguration.sendDelay
        )
        assertEquals(ENABLED_AUTO_TRACKING_DEFAULT, defaultWebtrekkConfiguration.autoTracking)
    }

    @Test
    fun `validate webtrekk configurations are set`() {
        assertEquals(webtrekkConfiguration.trackIds, listOf("123456789", "123"))
        assertEquals(webtrekkConfiguration.trackDomain, "www.webtrekk.com")
        assertEquals(webtrekkConfiguration.logLevel, LOG_LEVEL_DEFAULT) // Default value
        assertEquals(webtrekkConfiguration.sendDelay, TimeUnit.MINUTES.toMillis(20))
        assertEquals(webtrekkConfiguration.autoTracking, false)
    }
}
