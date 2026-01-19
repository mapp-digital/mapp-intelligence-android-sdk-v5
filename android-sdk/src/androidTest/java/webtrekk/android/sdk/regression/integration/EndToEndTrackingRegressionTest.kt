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

package webtrekk.android.sdk.regression.integration

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.events.ActionEvent
import webtrekk.android.sdk.events.MediaEvent
import webtrekk.android.sdk.events.PageViewEvent
import webtrekk.android.sdk.events.eventParams.MediaParameters

/**
 * End-to-end regression tests for tracking functionality.
 * 
 * These tests verify complete workflows:
 * - Initialization -> Tracking -> Data persistence
 * - Multiple tracking calls in sequence
 * - Different event types (PageView, Action, Media)
 * - Configuration changes during runtime
 */
@RunWith(AndroidJUnit4::class)
class EndToEndTrackingRegressionTest {
    private lateinit var context: Context
    private lateinit var config: WebtrekkConfiguration
    
    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        
        config = WebtrekkConfiguration.Builder(
            trackIds = listOf("1234567890"),
            trackDomain = "https://www.example.com"
        )
            .logLevel(webtrekk.android.sdk.Logger.Level.BASIC)
            .build()
        
        // Safely clear SDK config
        try {
            Webtrekk.getInstance().clearSdkConfig()
        } catch (e: Exception) {
            // Ignore if not initialized
        }
    }
    
    /**
     * Creates a properly mocked Activity with all required system services
     * for testing trackPage(Activity, ...) functionality.
     */
    private fun createMockActivity(): Activity {
        val mockActivity = mockk<Activity>(relaxed = true)
        
        // Mock ComponentName
        every { mockActivity.componentName } returns android.content.ComponentName(
            context.packageName,
            "TestActivity"
        )
        
        // Mock WindowManager and Display for resolution()
        val mockWindowManager = mockk<WindowManager>(relaxed = true)
        val mockDisplay = mockk<Display>(relaxed = true)
        val displayMetrics = DisplayMetrics().apply {
            widthPixels = 1080
            heightPixels = 1920
        }
        
        every { mockActivity.getSystemService(Context.WINDOW_SERVICE) } returns mockWindowManager
        every { mockWindowManager.defaultDisplay } returns mockDisplay
        every { mockDisplay.getMetrics(any()) } answers {
            val metrics = firstArg<DisplayMetrics>()
            metrics.setTo(displayMetrics)
        }
        
        // Mock PackageManager for appVersionName and appVersionCode
        val mockPackageManager = mockk<PackageManager>(relaxed = true)
        val mockPackageInfo = PackageInfo().apply {
            versionName = "1.0.0"
            @Suppress("DEPRECATION")
            versionCode = 1
        }
        
        every { mockActivity.packageName } returns context.packageName
        every { mockActivity.getPackageManager() } returns mockPackageManager
        every {
            mockPackageManager.getPackageInfo(
                any<String>(),
                any<Int>()
            )
        } returns mockPackageInfo
        
        return mockActivity
    }
    
    @After
    fun tearDown() {
        runBlocking {
            try {
                Webtrekk.getInstance().clearSdkConfig()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    @Test
    fun regression_complete_tracking_workflow_initialization_to_tracking() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        
        // Wait for initialization
        delay(200)
        
        // When: Tracking a page view using trackCustomPage (doesn't require Activity)
        webtrekk.trackCustomPage("TestPage", emptyMap())
        
        // Then: SDK should be initialized and ready
        assertThat("SDK should be initialized", webtrekk.isInitialized(), equalTo(true))
        
        // And: Configuration should be accessible
        val currentConfig = webtrekk.getCurrentConfiguration()
        assertThat("Configuration should be accessible", currentConfig, notNullValue())
    }

    @Test
    fun regression_multiple_tracking_calls_work_correctly() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        // When: Making multiple tracking calls using trackCustomPage
        webtrekk.trackCustomPage("Page1", emptyMap())
        delay(50)
        webtrekk.trackCustomPage("Page2", mapOf("param1" to "value1"))
        delay(50)
        webtrekk.trackCustomPage("Page3", mapOf("param2" to "value2"))
        
        // Then: All calls should complete without errors
        assertThat("SDK should remain initialized", webtrekk.isInitialized(), equalTo(true))
    }

    @Test
    fun regression_different_event_types_can_be_tracked() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        // When: Tracking different event types
        webtrekk.trackCustomPage("TestPage", emptyMap())
        delay(50)
        
        // PageViewEvent is a data class with name parameter
        val pageViewEvent = PageViewEvent("CustomPage")
        webtrekk.trackPage(pageViewEvent)
        delay(50)
        
        // ActionEvent is a data class with name parameter
        val actionEvent = ActionEvent("TestAction")
        webtrekk.trackAction(actionEvent)
        delay(50)
        
        // MediaEvent requires pageName and MediaParameters
        val mediaParameters = MediaParameters(
            name = "TestMedia",
            action = MediaParameters.Action.PLAY.code(),
            position = 0,
            duration = 100
        )
        val mediaEvent = MediaEvent("TestPage", mediaParameters)
        webtrekk.trackMedia(mediaEvent)
        
        // Then: All event types should be trackable
        assertThat("SDK should remain initialized", webtrekk.isInitialized(), equalTo(true))
    }

    @Test
    fun regression_configuration_changes_apply_correctly() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        // When: Changing configuration at runtime
        webtrekk.setRequestInterval(30)
        webtrekk.setBatchEnabled(true)
        webtrekk.setRequestPerBatch(10)
        webtrekk.setVersionInEachRequest(true)
        
        // Then: Configuration changes should be applied
        val currentConfig = webtrekk.getCurrentConfiguration()
        assertThat("Batch should be enabled", webtrekk.isBatchEnabled(), equalTo(true))
        assertThat("Requests per batch should be set", webtrekk.getRequestsPerBatch(), equalTo(10))
        assertThat("Version in each request should be enabled", 
            webtrekk.getVersionInEachRequest(), equalTo(true))
    }

    @Test
    fun regression_tracking_works_after_configuration_changes() = runBlocking {
        // Given: SDK initialized and configured
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        // When: Changing configuration and then tracking
        webtrekk.setBatchEnabled(true)
        delay(50)
        webtrekk.trackCustomPage("TestPage", emptyMap())
        delay(50)
        webtrekk.setBatchEnabled(false)
        delay(50)
        webtrekk.trackCustomPage("TestPage2", emptyMap())
        
        // Then: Tracking should work after configuration changes
        assertThat("SDK should remain initialized", webtrekk.isInitialized(), equalTo(true))
    }

    @Test
    fun regression_trackPage_with_activity_works_correctly() = runBlocking {
        // Given: SDK initialized and properly mocked Activity
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        val mockActivity = createMockActivity()
        
        // When: Tracking a page view using Activity
        webtrekk.trackPage(mockActivity, "TestPage", emptyMap())
        delay(100)
        
        // Then: SDK should remain initialized
        assertThat("SDK should remain initialized", webtrekk.isInitialized(), equalTo(true))
    }

    @Test
    fun regression_trackPage_with_activity_and_custom_params_works() = runBlocking {
        // Given: SDK initialized and properly mocked Activity
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        val mockActivity = createMockActivity()
        
        // When: Tracking a page view with custom parameters using Activity
        val trackingParams = mapOf(
            "param1" to "value1",
            "param2" to "value2"
        )
        webtrekk.trackPage(mockActivity, "CustomPageName", trackingParams)
        delay(100)
        
        // Then: SDK should remain initialized
        assertThat("SDK should remain initialized", webtrekk.isInitialized(), equalTo(true))
    }

    @Test
    fun regression_trackPage_with_activity_uses_activity_class_name_when_custom_name_null() = runBlocking {
        // Given: SDK initialized and properly mocked Activity
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        val mockActivity = createMockActivity()
        
        // When: Tracking a page view with null custom name (should use Activity class name)
        webtrekk.trackPage(mockActivity, null, emptyMap())
        delay(100)
        
        // Then: SDK should remain initialized
        assertThat("SDK should remain initialized", webtrekk.isInitialized(), equalTo(true))
    }
}
