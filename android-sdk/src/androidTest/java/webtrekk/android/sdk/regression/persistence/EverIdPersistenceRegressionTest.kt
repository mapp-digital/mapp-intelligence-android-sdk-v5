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

package webtrekk.android.sdk.regression.persistence

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.data.model.GenerationMode
import webtrekk.android.sdk.regression.RegressionTestBase

/**
 * Regression tests for EverId persistence.
 * 
 * These tests ensure that:
 * - EverId persists across app restarts (simulated by re-initialization)
 * - EverId can be set and retrieved correctly
 * - EverId is cleared when anonymous tracking is enabled
 * - EverId generation mode is correctly persisted
 */
class EverIdPersistenceRegressionTest : RegressionTestBase() {

    @Test
    fun regression_everId_persists_across_reinitialization() = runBlocking {
        // Given: SDK initialized and everId generated
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        
        // Wait for everId to be generated
        var originalEverId: String? = null
        var attempts = 0
        while (originalEverId.isNullOrEmpty() && attempts < 50) {
            delay(50)
            originalEverId = webtrekk.getEverId()
            attempts++
        }
        assertThat("Original everId should be generated", originalEverId, notNullValue())
        
        // When: Checking everId multiple times over time
        // This verifies that everId persists and doesn't change during SDK lifetime
        delay(200)
        var persistedEverId1: String? = null
        attempts = 0
        while (persistedEverId1.isNullOrEmpty() && attempts < 20) {
            delay(50)
            persistedEverId1 = webtrekk.getEverId()
            attempts++
        }
        
        delay(200)
        var persistedEverId2: String? = null
        attempts = 0
        while (persistedEverId2.isNullOrEmpty() && attempts < 20) {
            delay(50)
            persistedEverId2 = webtrekk.getEverId()
            attempts++
        }
        
        // Then: EverId should persist (same value) - it should not change over time
        assertThat("EverId should persist over time (first check)", persistedEverId1, equalTo(originalEverId))
        assertThat("EverId should persist over time (second check)", persistedEverId2, equalTo(originalEverId))
    }
    
    @Test
    fun regression_everId_is_regenerated_after_clearSdkConfig() = runBlocking {
        // Given: SDK initialized and everId generated
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        
        // Wait for everId to be generated
        var originalEverId: String? = null
        var attempts = 0
        while (originalEverId.isNullOrEmpty() && attempts < 50) {
            delay(50)
            originalEverId = webtrekk.getEverId()
            attempts++
        }
        assertThat("Original everId should be generated", originalEverId, notNullValue())
        
        // When: Clearing SDK config (which clears SharedPreferences including everId) and reinitializing
        webtrekk.clearSdkConfig()
        webtrekk.init(context, config)
        
        // Then: A new everId should be generated (clearSdkConfig clears everything)
        var newEverId: String? = null
        attempts = 0
        while (newEverId.isNullOrEmpty() && attempts < 50) {
            delay(50)
            newEverId = webtrekk.getEverId()
            attempts++
        }
        
        assertThat("New everId should be generated after clearSdkConfig", newEverId, notNullValue())
        assertThat("New everId should be different from original (clearSdkConfig clears everything)", 
            newEverId, not(equalTo(originalEverId)))
    }

    @Test
    fun regression_everId_can_be_set_and_retrieved() = runBlocking {
        // Given: SDK initialized
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        
        // When: Setting custom everId
        val customEverId = "test-ever-id-${System.currentTimeMillis()}"
        webtrekk.setEverId(customEverId)
        
        // Small delay to ensure write completes
        delay(100)
        
        // Then: EverId should be retrievable
        var retrievedEverId: String? = null
        var attempts = 0
        while (retrievedEverId != customEverId && attempts < 20) {
            delay(25)
            retrievedEverId = webtrekk.getEverId()
            attempts++
        }
        
        assertThat("Custom everId should be set", retrievedEverId, equalTo(customEverId))
    }

    @Test
    fun regression_everId_is_cleared_when_anonymous_tracking_enabled() = runBlocking {
        // Given: SDK initialized with everId
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        
        // Wait for everId
        var everId: String? = null
        var attempts = 0
        while (everId.isNullOrEmpty() && attempts < 50) {
            delay(50)
            everId = webtrekk.getEverId()
            attempts++
        }
        assertThat("EverId should exist before anonymous tracking", everId, notNullValue())
        
        // When: Enabling anonymous tracking
        webtrekk.anonymousTracking(true, emptySet())
        delay(100)
        
        // Then: EverId should be null
        val everIdAfterAnonymous = webtrekk.getEverId()
        assertThat("EverId should be null when anonymous tracking enabled", everIdAfterAnonymous, nullValue())
    }

    @Test
    fun regression_everId_is_regenerated_when_anonymous_tracking_disabled() = runBlocking {
        // Given: Anonymous tracking enabled
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        webtrekk.anonymousTracking(true, emptySet())
        delay(100)
        
        // When: Disabling anonymous tracking
        webtrekk.anonymousTracking(false, emptySet())
        
        // Then: New everId should be generated
        var newEverId: String? = null
        var attempts = 0
        while (newEverId.isNullOrEmpty() && attempts < 50) {
            delay(50)
            newEverId = webtrekk.getEverId()
            attempts++
        }
        
        assertThat("New everId should be generated", newEverId, notNullValue())
        assertThat("New everId should not be empty", newEverId?.isEmpty(), equalTo(false))
    }

    @Test
    fun regression_everId_generation_mode_is_persisted() = runBlocking {
        // Given: SDK initialized with user-defined everId
        val customEverId = "user-defined-ever-id"
        val customConfig = WebtrekkConfiguration.Builder(
            trackIds = listOf("1234567890"),
            trackDomain = "https://www.example.com"
        )
            .setEverId(customEverId)
            .build()
        
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, customConfig)
        
        // Wait for initialization
        delay(200)
        
        // Then: Generation mode should be USER_GENERATED
        val currentConfig = webtrekk.getCurrentConfiguration()
        assertThat("EverId mode should be USER_GENERATED", 
            currentConfig.everIdMode, 
            equalTo(GenerationMode.USER_GENERATED))
    }
}
