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

package webtrekk.android.sdk.domain

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.core.SessionsImpl
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.dao.TrackRequestDao

internal class SessionsTest {

    private lateinit var webtrekkSharedPrefs: WebtrekkSharedPrefs
    private lateinit var trackRequestDao: TrackRequestDao
    private lateinit var sessions: SessionsImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        webtrekkSharedPrefs = mockk(relaxed = true)
        trackRequestDao = mockk(relaxed = true)
        sessions = SessionsImpl(webtrekkSharedPrefs, trackRequestDao, Dispatchers.Unconfined)
    }

    @Test
    fun `getAppFirstOpen returns current value and resets when requested`() {
        var storedFirstOpen = "1"
        io.mockk.every { webtrekkSharedPrefs.appFirstOpen } answers { storedFirstOpen }
        io.mockk.every { webtrekkSharedPrefs.appFirstOpen = any() } answers { storedFirstOpen = firstArg() }

        val firstValue = sessions.getAppFirstOpen(updateValue = true)

        assertThat(firstValue).isEqualTo("1")
        assertThat(storedFirstOpen).isEqualTo("0")
    }

    @Test
    fun `start and get current session updates flag`() {
        var fns = "0"
        io.mockk.every { webtrekkSharedPrefs.fns } answers { fns }
        io.mockk.every { webtrekkSharedPrefs.fns = any() } answers { fns = firstArg() }

        sessions.startNewSession()
        assertThat(fns).isEqualTo("1")

        val currentSession = sessions.getCurrentSession()
        assertThat(currentSession).isEqualTo("1")
        assertThat(fns).isEqualTo("0")
        verify { webtrekkSharedPrefs.fns = "1" }
    }
}
