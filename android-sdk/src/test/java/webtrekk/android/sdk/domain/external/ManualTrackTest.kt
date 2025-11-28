package webtrekk.android.sdk.domain.external

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.util.cacheTrackRequestParams
import webtrekk.android.sdk.util.cacheTrackRequestWithCustomParamsParams
import webtrekk.android.sdk.util.coroutinesDispatchersProvider
import webtrekk.android.sdk.util.dataTrack
import webtrekk.android.sdk.util.trackRequest
import webtrekk.android.sdk.util.trackingParams

@ExperimentalCoroutinesApi
internal class ManualTrackTest : BaseExternalTest() {
    @RelaxedMockK
    lateinit var sessions: Sessions

    lateinit var cacheTrackRequest: CacheTrackRequest

    lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    lateinit var manualTrack: ManualTrack

    @Before
    override fun setup() {
        super.setup()
    }

    @Before
    fun resetRequests() {
        mockkObject(AppModule)
        every { AppModule.logger } returns mockk(relaxed = true)

        cacheTrackRequest = mockk()

        cacheTrackRequestWithCustomParams = mockk()

        every { sessions.getUrlKey() } returns emptyMap()

        manualTrack = ManualTrack(
            coroutineContext = coroutineScope.coroutineContext,
            sessions = sessions,
            cacheTrackRequest = cacheTrackRequest,
            cacheTrackRequestWithCustomParams = cacheTrackRequestWithCustomParams
        )
    }

    @Test
    fun `if opt out is active or auto tracking is enabled then return and don't track`() =
        runTest {
            val params = ManualTrack.Params(trackRequest, emptyMap(), true, true)

            manualTrack(params, coroutinesDispatchersProvider())

            coVerify(exactly = 0) {
                cacheTrackRequest(cacheTrackRequestParams)
                cacheTrackRequestWithCustomParams(cacheTrackRequestWithCustomParamsParams)
            }
        }

    @Test
    fun `verify that cacheTrackRequest is called when there is no tracking params attached`() =
        runTest {
            val result = Result.success(trackRequest)

            coEvery { cacheTrackRequest(cacheTrackRequestParams) } returns result

            val params = ManualTrack.Params(trackRequest, emptyMap(), false, false)

            manualTrack(params, coroutinesDispatchersProvider())

            coVerify(exactly = 1) {
                cacheTrackRequest(cacheTrackRequestParams)
            }
        }

    @Test
    fun `verify that cacheTrackRequestWithCustomParams is called when there are tracking params`() =
        runTest {
            val resultSuccess = Result.success(dataTrack)

            coEvery { cacheTrackRequestWithCustomParams(cacheTrackRequestWithCustomParamsParams) } returns resultSuccess

            val params = ManualTrack.Params(trackRequest, trackingParams, false, false)

            manualTrack(params, coroutinesDispatchersProvider())

            coVerify(exactly = 1) {
                cacheTrackRequestWithCustomParams(cacheTrackRequestWithCustomParamsParams)
            }
            assertThat(params.isOptOut).isFalse()
        }
}
