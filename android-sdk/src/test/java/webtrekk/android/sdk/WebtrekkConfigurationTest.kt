package webtrekk.android.sdk

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class WebtrekkConfigurationTest {

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

        assertEquals(DefaultConfiguration.logLevel, defaultWebtrekkConfiguration.logLevel)
        assertEquals(
            DefaultConfiguration.timeUnit.toMillis(DefaultConfiguration.sendDelay),
            defaultWebtrekkConfiguration.sendDelay
        )
        assertEquals(DefaultConfiguration.enabledAutoTrack, defaultWebtrekkConfiguration.autoTracking)
        assertEquals(DefaultConfiguration.workManagerConstraints, defaultWebtrekkConfiguration.workManagerConstraints)
    }

    @Test
    fun `validate webtrekk configurations are set`() {
        assertEquals(webtrekkConfiguration.trackIds, listOf("123456789", "123"))
        assertEquals(webtrekkConfiguration.trackDomain, "www.webtrekk.com")
        assertEquals(webtrekkConfiguration.logLevel, DefaultConfiguration.logLevel) // DefaultConfiguration value
        assertEquals(webtrekkConfiguration.sendDelay, TimeUnit.MINUTES.toMillis(20))
        assertEquals(webtrekkConfiguration.autoTracking, false)
    }
}
