package webtrekk.android.sdk.domain.external

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.util.cacheTrackRequestParams
import webtrekk.android.sdk.util.cacheTrackRequestWithCustomParamsParams
import webtrekk.android.sdk.util.coroutinesDispatchersProvider
import webtrekk.android.sdk.util.dataTrack
import webtrekk.android.sdk.util.trackRequest
import webtrekk.android.sdk.util.trackingParams

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ManualTrackTest : BaseExternalTest() {

    @RelaxedMockK
    lateinit var sessions: Sessions

    lateinit var cacheTrackRequest: CacheTrackRequest

    lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    private lateinit var manualTrack: ManualTrack

    @BeforeAll
    override fun setup() {
        super.setup()
    }

    @BeforeEach
    fun resetRequests() {
        cacheTrackRequest = mockk()

        cacheTrackRequestWithCustomParams = mockk()

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
        }
}