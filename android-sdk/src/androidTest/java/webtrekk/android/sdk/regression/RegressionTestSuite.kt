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
 */

package webtrekk.android.sdk.regression

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration

/**
 * Base class for regression tests.
 * 
 * Regression tests verify that previously working functionality continues to work
 * after code changes. They should be comprehensive, deterministic, and fast.
 * 
 * This base class provides common setup and teardown for all regression tests.
 */
@RunWith(AndroidJUnit4::class)
abstract class RegressionTestBase {
    protected lateinit var context: Context
    protected lateinit var config: WebtrekkConfiguration
    
    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        config = WebtrekkConfiguration.Builder(
            trackIds = listOf("1234567890"),
            trackDomain = "https://www.example.com"
        )
            .logLevel(webtrekk.android.sdk.Logger.Level.BASIC)
            .build()
        
        // Ensure clean state before each test
        // Use try-catch to safely clear config even if SDK is not initialized
        try {
            Webtrekk.getInstance().clearSdkConfig()
        } catch (e: IllegalStateException) {
            // If SDK is not initialized, clearSdkConfig might fail
            // This is okay - we'll initialize it in the test
        } catch (e: Exception) {
            // Ignore any other errors during cleanup
        }
    }
    
    @After
    fun tearDown() {
        runBlocking {
            // Clean up after each test
            try {
                Webtrekk.getInstance().clearSdkConfig()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
    }
    
    /**
     * Helper to initialize Webtrekk with default test configuration
     */
    protected fun initializeWebtrekk() {
        Webtrekk.getInstance().init(context, config)
    }
    
    /**
     * Helper to wait for async operations to complete
     */
    protected suspend fun waitForAsyncOperations(delayMs: Long = 200) {
        kotlinx.coroutines.delay(delayMs)
    }
}
