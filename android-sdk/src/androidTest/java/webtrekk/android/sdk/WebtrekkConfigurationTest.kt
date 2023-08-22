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

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.model.GenerationMode

internal class WebtrekkConfigurationTest {

    lateinit var appContext: Context
    lateinit var webtrekkSharedPrefs: WebtrekkSharedPrefs

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().context
        webtrekkSharedPrefs = WebtrekkSharedPrefs(context = appContext)
        Webtrekk.getInstance().init(appContext, webtrekkConfigurationBuilder.build())
    }

    @After
    fun tearDown() {
        Webtrekk.reset(appContext)
        webtrekkSharedPrefs.sharedPreferences.edit().clear().apply()
    }

    @Test
    fun throw_error_if_trackIds_has_null_or_empty_values() {
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
    fun throw_error_if_trackDomain_is_null_or_blank() {
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
    fun test_default_values() {
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
    fun test_webtrekk_configurations_are_set() {
        val webtrekkConfiguration =
            WebtrekkConfiguration.Builder(listOf("123456789", "123"), "www.webtrekk.com")
                .requestsInterval(interval = 20, timeUnit = TimeUnit.MINUTES)
                .disableAutoTracking()
                .setUserMatchingEnabled(false)
                .build()
        Webtrekk.getInstance().init(appContext, webtrekkConfiguration)
        assertEquals(webtrekkConfiguration.trackIds, listOf("123456789", "123"))
        assertEquals(webtrekkConfiguration.trackDomain, "www.webtrekk.com")
        assertEquals(
            webtrekkConfiguration.logLevel,
            DefaultConfiguration.LOG_LEVEL_VALUE
        )
        assertEquals(webtrekkConfiguration.requestsInterval, 20)
        assertEquals(webtrekkConfiguration.autoTracking, false)
        assertEquals(webtrekkConfiguration.fragmentsAutoTracking, false)
        assertEquals(
            webtrekkConfiguration.workManagerConstraints,
            DefaultConfiguration.WORK_MANAGER_CONSTRAINTS
        )
        assertEquals(webtrekkConfiguration.okHttpClient, DefaultConfiguration.OKHTTP_CLIENT)
    }

    @Test
    fun test_fragments_auto_track_is_disabled_when_auto_track_is_disabled() {
        val defaultWebtrekkConfiguration =
            WebtrekkConfiguration.Builder(listOf("123"), "www.webtrekk.com")
                .disableAutoTracking().build()

        assertEquals(defaultWebtrekkConfiguration.fragmentsAutoTracking, false)
    }

    @Test
    fun test_not_storing_everId_when_anonymous_tracking_enabled() {
        val webtrekkConfiguration = webtrekkConfigurationBuilder
            .setEverId("1111")
            .build()

        Webtrekk.getInstance().init(context = appContext, webtrekkConfiguration)
        Webtrekk.getInstance().anonymousTracking(true, emptySet())

        assertThat(Webtrekk.getInstance().getEverId(), nullValue())
    }

    @Test
    fun test_autogenerating_everId() = runBlocking(Dispatchers.Unconfined) {
        Webtrekk.getInstance().clearSdkConfig()
        val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds, trackDomain)
            .build()
        Webtrekk.getInstance().init(context = appContext, config = webtrekkConfiguration)
        delay(100)
        val everId = Webtrekk.getInstance().getEverId()
        print("everId: $everId")
        assertThat(everId, not(""))
        assertThat(everId, notNullValue())
    }

    @Test
    fun test_user_configured_everId() = runBlocking{
        Webtrekk.getInstance().clearSdkConfig()
        val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds, trackDomain)
            .setEverId("2222")
            .build()
        Webtrekk.getInstance().init(context = appContext, config = webtrekkConfiguration)
        //Webtrekk.getInstance().setEverId("2222")
        delay(100)
        val everId = Webtrekk.getInstance().getCurrentConfiguration().everId
        val mode=Webtrekk.getInstance().getCurrentConfiguration().everIdMode

        assertThat(everId, equalTo("2222"))
        assertThat(mode, equalTo(GenerationMode.USER_GENERATED))
    }

    @Test
    fun test_set_temporary_session_id() = runBlocking{
        Webtrekk.reset(appContext)
        val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds, trackDomain)
            .build()
        Webtrekk.getInstance().init(context = appContext, config = webtrekkConfiguration)
        Webtrekk.getInstance().anonymousTracking(true, emptySet())
        Webtrekk.getInstance().setTemporarySessionId("user_xyz_123")

        val tempSessionId= Webtrekk.getInstance().getCurrentConfiguration().temporarySessionId
        assertThat(tempSessionId, equalTo("user_xyz_123"))
    }

    @Test
    fun testToJsonMethod() {
        val webtrekkConfiguration = WebtrekkConfiguration.Builder(
            trackDomain = "www.webtrekk.com",
            trackIds = listOf("123")
        )
            .build()
        val json = webtrekkConfiguration.toJson()
        val config = WebtrekkConfiguration.fromJson(json)
        assertEquals(config.trackIds, webtrekkConfiguration.trackIds)
    }
}