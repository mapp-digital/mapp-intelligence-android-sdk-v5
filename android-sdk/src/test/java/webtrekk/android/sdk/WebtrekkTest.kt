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

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertThrows
import webtrekk.android.sdk.module.LibraryModule

/**
 * Verifies that calling SDK methods before init() throws IllegalStateException.
 *
 * We mock LibraryModule so that accessing .configuration (which hasOptOut() reaches via
 * the config getter) throws IllegalStateException — matching the real production behaviour —
 * without touching the real InteractorModule / DataModule / Room. Without this isolation,
 * accessing the real InteractorModule.optOut → sessions → DataModule.database → Room on
 * Dispatchers.Default throws a NullPointerException that leaks as UncaughtExceptionsBeforeTest
 * into the next test class that uses runTest.
 */
internal class WebtrekkTest {

    @Before
    fun setUp() {
        mockkObject(LibraryModule)
        every { LibraryModule.isInitialized() } returns false
        every { LibraryModule.application } throws IllegalStateException("Webtrekk not initialized")
        every { LibraryModule.configuration } throws IllegalStateException("Webtrekk not initialized")
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `throws IllegalStateException when init not called`() {
        val webtrekk = Webtrekk.getInstance()
        assertThrows(IllegalStateException::class.java) {
            webtrekk.hasOptOut()
        }
    }
}
