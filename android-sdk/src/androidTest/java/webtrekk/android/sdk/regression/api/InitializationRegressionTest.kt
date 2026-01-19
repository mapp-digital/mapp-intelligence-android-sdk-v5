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

package webtrekk.android.sdk.regression.api

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.regression.RegressionTestBase

/**
 * Regression tests for SDK initialization.
 * 
 * These tests ensure that:
 * - SDK can be initialized with valid configuration
 * - Initialization state is correctly tracked
 * - Multiple initialization attempts are handled properly
 * - Configuration is correctly applied
 */
class InitializationRegressionTest : RegressionTestBase() {

    @Test
    fun regression_sdk_initializes_with_valid_configuration() = runBlocking {
        // Given: Valid configuration
        val webtrekk = Webtrekk.getInstance()
        
        // When: Initializing SDK
        webtrekk.init(context, config)
        
        // Then: SDK should be initialized
        assertThat("SDK should be initialized", webtrekk.isInitialized(), equalTo(true))
        
        // And: Configuration should be accessible
        val currentConfig = webtrekk.getCurrentConfiguration()
        assertThat("Configuration should not be null", currentConfig, notNullValue())
        assertThat("Track IDs should match", currentConfig.trackIds, equalTo(config.trackIds))
        assertThat("Track domain should match", currentConfig.trackDomains, equalTo(config.trackDomain))
    }

    @Test
    fun regression_sdk_handles_multiple_initialization_attempts() = runBlocking {
        // Given: SDK initialized once
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        
        // When: Attempting to initialize again
        val secondConfig = WebtrekkConfiguration.Builder(
            trackIds = listOf("9876543210"),
            trackDomain = "https://www.other.com"
        ).build()
        
        // Should not throw exception (should log warning instead)
        webtrekk.init(context, secondConfig)
        
        // Then: Original configuration should remain
        val currentConfig = webtrekk.getCurrentConfiguration()
        assertThat("Original track IDs should remain", currentConfig.trackIds, equalTo(config.trackIds))
        assertThat("Original track domain should remain", currentConfig.trackDomains, equalTo(config.trackDomain))
    }

    @Test
    fun regression_sdk_everId_is_generated_after_initialization() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        
        // When: Waiting for async initialization to complete
        var everId: String? = null
        var attempts = 0
        while (everId.isNullOrEmpty() && attempts < 50) {
            delay(50)
            everId = webtrekk.getEverId()
            attempts++
        }
        
        // Then: EverId should be generated
        assertThat("EverId should be generated", everId, notNullValue())
        assertThat("EverId should not be empty", everId?.isEmpty(), equalTo(false))
    }

    @Test
    fun regression_sdk_initialization_with_custom_everId() = runBlocking {
        // Given: Configuration with custom everId
        val customEverId = "custom-ever-id-12345"
        val customConfig = WebtrekkConfiguration.Builder(
            trackIds = listOf("1234567890"),
            trackDomain = "https://www.example.com"
        )
            .setEverId(customEverId)
            .build()
        
        val webtrekk = Webtrekk.getInstance()
        
        // When: Initializing with custom everId
        webtrekk.init(context, customConfig)
        
        // Then: Custom everId should be set
        var everId: String? = null
        var attempts = 0
        while (everId != customEverId && attempts < 50) {
            delay(50)
            everId = webtrekk.getEverId()
            attempts++
        }
        
        assertThat("Custom everId should be set", everId, equalTo(customEverId))
    }

    @Test
    fun regression_sdk_getInstance_returns_same_instance() {
        // Given: Multiple calls to getInstance
        val instance1 = Webtrekk.getInstance()
        val instance2 = Webtrekk.getInstance()
        
        // Then: Should return the same instance (singleton)
        assertThat("Should return same instance", instance1, equalTo(instance2))
    }

    @Test
    fun regression_sdk_trackIds_and_domain_are_accessible_after_init() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        
        // Then: Track IDs and domain should be accessible
        assertThat("Track IDs should be accessible", webtrekk.getTrackIds(), equalTo(config.trackIds))
        assertThat("Track domain should be accessible", webtrekk.getTrackDomain(), equalTo(config.trackDomain))
    }
}
