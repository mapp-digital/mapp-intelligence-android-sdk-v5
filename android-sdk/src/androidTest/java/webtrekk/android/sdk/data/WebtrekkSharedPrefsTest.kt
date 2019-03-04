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

package webtrekk.android.sdk.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

internal class WebtrekkSharedPrefsTest {

    private lateinit var webtrekkSharedPrefs: WebtrekkSharedPrefs

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        webtrekkSharedPrefs = WebtrekkSharedPrefs(context)
    }

    @Test
    fun testEverId() {
        // Verify default value
        assertThat(webtrekkSharedPrefs.everId, `is`(""))

        webtrekkSharedPrefs.everId = "1"

        // Verify ever id is set correctly
        assertThat(webtrekkSharedPrefs.everId, `is`("1"))
    }

    @Test
    fun testAppFirstStart() {
        // Verify default value
        assertThat(webtrekkSharedPrefs.appFirstStart, `is`("0"))

        webtrekkSharedPrefs.appFirstStart = "1"

        // Verify appFirstStart is set correctly
        assertThat(webtrekkSharedPrefs.appFirstStart, `is`("1"))
    }

    @Test
    fun testFns() {
        // Verify default value
        assertThat(webtrekkSharedPrefs.fns, `is`("0"))

        webtrekkSharedPrefs.fns = "1"

        // Verify fns is set correctly
        assertThat(webtrekkSharedPrefs.fns, `is`("1"))
    }

    @Test
    fun testOptOut() {
        // Verify default value
        assertThat(webtrekkSharedPrefs.optOut, `is`(false))

        webtrekkSharedPrefs.optOut = true

        // Verify optOut is set correctly
        assertThat(webtrekkSharedPrefs.optOut, `is`(true))
    }
}