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

package webtrekk.android.sdk.domain.external

import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.util.cacheTrackRequestWithCustomParamsParams
import webtrekk.android.sdk.util.coroutinesDispatchersProvider
import webtrekk.android.sdk.util.trackRequest
import webtrekk.android.sdk.util.trackingParams

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TrackCustomPageTest : BaseExternalTest() {

    @RelaxedMockK
    lateinit var sessions: Sessions

    @RelaxedMockK
    lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    lateinit var trackCustomPage: TrackCustomPage

    @BeforeAll
    override fun setup() {
        super.setup()
        trackCustomPage = TrackCustomPage(
            coroutineContext,
            sessions,
            cacheTrackRequestWithCustomParams
        )
    }

    @Test
    fun `if opt out is active then return and don't track`() = runTest {
        val params = TrackCustomPage.Params(
            trackRequest = trackRequest,
            trackingParams = trackingParams,
            isOptOut = true
        )

        trackCustomPage(params, coroutinesDispatchersProvider())

        coVerify(exactly = 0) {
            cacheTrackRequestWithCustomParams(cacheTrackRequestWithCustomParamsParams)
        }
    }

    @Test
    fun `verify that cacheTrackRequestWithCustomParams is called for tracking custom page`() =
        runTest {
            val params = TrackCustomPage.Params(
                trackRequest = trackRequest,
                trackingParams = trackingParams,
                isOptOut = false
            )

            trackCustomPage(params, coroutinesDispatchersProvider())

            coVerify(exactly = 1) {
                cacheTrackRequestWithCustomParams(cacheTrackRequestWithCustomParamsParams)
            }
        }
}