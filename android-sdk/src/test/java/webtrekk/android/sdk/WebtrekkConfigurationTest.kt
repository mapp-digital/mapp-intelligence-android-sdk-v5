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

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.fail
import org.junit.Assert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit

class WebtrekkConfigurationTest {

    private val webtrekkConfiguration =
        WebtrekkConfiguration.Builder(listOf("123456789", "123"), "www.webtrekk.com")
            .requestsInterval(interval = 20)
            .disableAutoTracking()
            .build()

    @Test
    fun `throw error if trackIds has null or empty values`() {
        val errorMsg =
            "trackIds is missing in the configurations. trackIds is required in the configurations."

        try {
            val configuration = WebtrekkConfiguration.Builder(listOf(), "www.webtrekk.com").build()
            // Invoke trackIds
            configuration.trackIds
            fail("Expected an IllegalStateException to be thrown!")
        } catch (e: IllegalStateException) {
            assertThat(e.message, `is`(errorMsg))
        }
    }

    @Test
    fun `throw error if trackDomain is null or blank`() {
        val errorMsg =
            "trackDomain is missing in the configurations. trackDomain is required in the configurations."

        try {
            val configuration = WebtrekkConfiguration.Builder(listOf("123"), "").build()
            // Invoke trackDomain
            configuration.trackDomain
            fail("Expected an IllegalStateException to be thrown!")
        } catch (e: IllegalStateException) {
            assertThat(e.message, `is`(errorMsg))
        }
    }

    @Test
    fun `test default values`() {
        val defaultWebtrekkConfiguration =
            WebtrekkConfiguration.Builder(listOf("123"), "www.webtrekk.com").build()

        val expectedWebtrekkConfiguration =
            WebtrekkConfiguration.Builder(listOf("123"), "www.webtrekk.com")
                .logLevel(DefaultConfiguration.LOG_LEVEL_VALUE)
                .requestsInterval(interval = DefaultConfiguration.REQUESTS_INTERVAL)
                .workManagerConstraints(DefaultConfiguration.WORK_MANAGER_CONSTRAINTS)
                .okHttpClient(DefaultConfiguration.OKHTTP_CLIENT).build()

        assertEquals(defaultWebtrekkConfiguration, expectedWebtrekkConfiguration)
    }

    @Test
    fun `test webtrekk configurations are set`() {
        assertEquals(webtrekkConfiguration.trackIds, listOf("123456789", "123"))
        assertEquals(webtrekkConfiguration.trackDomain, "www.webtrekk.com")
        assertEquals(
            webtrekkConfiguration.logLevel,
            DefaultConfiguration.LOG_LEVEL_VALUE
        )
        assertEquals(webtrekkConfiguration.requestsInterval, TimeUnit.MINUTES.toMillis(20))
        assertEquals(webtrekkConfiguration.autoTracking, false)
        assertEquals(
            webtrekkConfiguration.workManagerConstraints,
            DefaultConfiguration.WORK_MANAGER_CONSTRAINTS
        )
        assertEquals(webtrekkConfiguration.okHttpClient, DefaultConfiguration.OKHTTP_CLIENT)
    }
}
