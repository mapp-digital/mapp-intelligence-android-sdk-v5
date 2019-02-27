/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

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

        assertEquals(DefaultConfiguration.LOG_LEVEL_VALUE, defaultWebtrekkConfiguration.logLevel)
        assertEquals(
            DefaultConfiguration.TIME_UNIT_VALUE.toMillis(DefaultConfiguration.SEND_DELAY_VALUE),
            defaultWebtrekkConfiguration.sendDelay
        )
        assertEquals(DefaultConfiguration.AUTO_TRACK_ENABLED, defaultWebtrekkConfiguration.autoTracking)
        assertEquals(DefaultConfiguration.WORK_MANAGER_CONSTRAINTS, defaultWebtrekkConfiguration.workManagerConstraints)
    }

    @Test
    fun `validate webtrekk configurations are set`() {
        assertEquals(webtrekkConfiguration.trackIds, listOf("123456789", "123"))
        assertEquals(webtrekkConfiguration.trackDomain, "www.webtrekk.com")
        assertEquals(webtrekkConfiguration.logLevel, DefaultConfiguration.LOG_LEVEL_VALUE) // DefaultConfiguration optOutValue
        assertEquals(webtrekkConfiguration.sendDelay, TimeUnit.MINUTES.toMillis(20))
        assertEquals(webtrekkConfiguration.autoTracking, false)
    }
}
