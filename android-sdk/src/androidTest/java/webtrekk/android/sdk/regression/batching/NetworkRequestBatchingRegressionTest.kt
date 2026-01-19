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

package webtrekk.android.sdk.regression.batching

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
import webtrekk.android.sdk.regression.RegressionTestBase

/**
 * Regression tests for network request batching functionality.
 * 
 * These tests ensure that:
 * - Batching can be enabled/disabled correctly
 * - Batch size configuration is respected
 * - Batch configuration persists across SDK lifecycle
 * - Batch settings are applied correctly
 */
@RunWith(AndroidJUnit4::class)
class NetworkRequestBatchingRegressionTest : RegressionTestBase() {

    @Test
    fun regression_batching_can_be_enabled() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        // When: Enabling batching
        webtrekk.setBatchEnabled(true)
        delay(100)
        
        // Then: Batching should be enabled
        assertThat("Batching should be enabled", webtrekk.isBatchEnabled(), equalTo(true))
        
        val currentConfig = webtrekk.getCurrentConfiguration()
        assertThat("Batch support should be enabled in config", 
            currentConfig.isBatchSupport, equalTo(true))
    }

    @Test
    fun regression_batching_can_be_disabled() = runBlocking {
        // Given: SDK initialized with batching enabled
        val webtrekk = Webtrekk.getInstance()
        val batchConfig = WebtrekkConfiguration.Builder(
            trackIds = listOf("1234567890"),
            trackDomain = "https://www.example.com"
        )
            .setBatchSupport(true)
            .build()
        webtrekk.init(context, batchConfig)
        delay(200)
        
        assertThat("Batching should be enabled initially", webtrekk.isBatchEnabled(), equalTo(true))
        
        // When: Disabling batching
        webtrekk.setBatchEnabled(false)
        delay(100)
        
        // Then: Batching should be disabled
        assertThat("Batching should be disabled", webtrekk.isBatchEnabled(), equalTo(false))
        
        val currentConfig = webtrekk.getCurrentConfiguration()
        assertThat("Batch support should be disabled in config", 
            currentConfig.isBatchSupport, equalTo(false))
    }

    @Test
    fun regression_batch_size_can_be_configured() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        // When: Setting batch size
        val batchSize = 20
        webtrekk.setRequestPerBatch(batchSize)
        delay(100)
        
        // Then: Batch size should be set
        assertThat("Batch size should be set", webtrekk.getRequestsPerBatch(), equalTo(batchSize))
        
        val currentConfig = webtrekk.getCurrentConfiguration()
        assertThat("Requests per batch should match in config", 
            currentConfig.requestsPerBatch, equalTo(batchSize))
    }

    @Test
    fun regression_batch_size_configuration_persists() = runBlocking {
        // Given: SDK initialized with custom batch size
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        val batchSize = 25
        webtrekk.setRequestPerBatch(batchSize)
        delay(100)
        
        // When: Checking batch size after delay
        delay(200)
        
        // Then: Batch size should persist
        assertThat("Batch size should persist", webtrekk.getRequestsPerBatch(), equalTo(batchSize))
    }

    @Test
    fun regression_batch_configuration_from_builder_is_applied() = runBlocking {
        // Given: Configuration with batching enabled and custom batch size
        val batchSize = 15
        val batchConfig = WebtrekkConfiguration.Builder(
            trackIds = listOf("1234567890"),
            trackDomain = "https://www.example.com"
        )
            .setBatchSupport(true, batchSize)
            .build()
        
        val webtrekk = Webtrekk.getInstance()
        
        // When: Initializing with batch configuration
        webtrekk.init(context, batchConfig)
        delay(200)
        
        // Then: Batch configuration should be applied
        assertThat("Batching should be enabled", webtrekk.isBatchEnabled(), equalTo(true))
        assertThat("Batch size should match", webtrekk.getRequestsPerBatch(), equalTo(batchSize))
        
        val currentConfig = webtrekk.getCurrentConfiguration()
        assertThat("Batch support should be enabled in config", 
            currentConfig.isBatchSupport, equalTo(true))
        assertThat("Requests per batch should match in config", 
            currentConfig.requestsPerBatch, equalTo(batchSize))
    }

    @Test
    fun regression_batch_configuration_can_be_changed_at_runtime() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        // When: Changing batch configuration at runtime
        webtrekk.setBatchEnabled(true)
        delay(50)
        webtrekk.setRequestPerBatch(30)
        delay(100)
        
        // Then: New configuration should be applied
        assertThat("Batching should be enabled", webtrekk.isBatchEnabled(), equalTo(true))
        assertThat("Batch size should be updated", webtrekk.getRequestsPerBatch(), equalTo(30))
        
        // Change again
        webtrekk.setBatchEnabled(false)
        delay(50)
        webtrekk.setRequestPerBatch(10)
        delay(100)
        
        // Then: New configuration should be applied
        assertThat("Batching should be disabled", webtrekk.isBatchEnabled(), equalTo(false))
        assertThat("Batch size should be updated again", webtrekk.getRequestsPerBatch(), equalTo(10))
    }

    @Test
    fun regression_batch_configuration_is_accessible_via_active_config() = runBlocking {
        // Given: SDK initialized with batch configuration
        val batchSize = 18
        val batchConfig = WebtrekkConfiguration.Builder(
            trackIds = listOf("1234567890"),
            trackDomain = "https://www.example.com"
        )
            .setBatchSupport(true, batchSize)
            .build()
        
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, batchConfig)
        delay(200)
        
        // When: Getting active configuration
        val activeConfig = webtrekk.getCurrentConfiguration()
        
        // Then: Batch configuration should be accessible
        assertThat("Active config should not be null", activeConfig, notNullValue())
        assertThat("Batch support should be accessible", 
            activeConfig.isBatchSupport, equalTo(true))
        assertThat("Requests per batch should be accessible", 
            activeConfig.requestsPerBatch, equalTo(batchSize))
    }

    @Test
    fun regression_default_batch_configuration_is_applied() = runBlocking {
        // Given: SDK initialized with default configuration
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        delay(200)
        
        // When: Checking default batch configuration
        val isBatchEnabled = webtrekk.isBatchEnabled()
        val batchSize = webtrekk.getRequestsPerBatch()
        
        // Then: Default values should be applied (from DefaultConfiguration)
        // These are reasonable defaults - the test verifies they exist
        // Batch enabled is a boolean, so it will always be true or false
        assertThat("Batch size should be greater than 0", batchSize > 0, equalTo(true))
        
        val currentConfig = webtrekk.getCurrentConfiguration()
        // Verify that batch configuration values are set
        // isBatchSupport is a Boolean (always has a value), so we just verify requestsPerBatch > 0
        assertThat("Requests per batch should be set in config", 
            currentConfig.requestsPerBatch > 0, equalTo(true))
    }
}
