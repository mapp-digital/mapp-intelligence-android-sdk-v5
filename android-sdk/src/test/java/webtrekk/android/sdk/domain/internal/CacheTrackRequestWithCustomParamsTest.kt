package webtrekk.android.sdk.domain.internal

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams.Params
import webtrekk.android.sdk.extension.toCustomParams
import webtrekk.android.sdk.util.cacheTrackRequestWithCustomParamsParams
import webtrekk.android.sdk.util.customParams
import webtrekk.android.sdk.util.trackRequest
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class CacheTrackRequestWithCustomParamsTest {

    @MockK
    lateinit var trackRequestRepository: TrackRequestRepository

    @MockK
    lateinit var customParamRepository: CustomParamRepository

    private lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        cacheTrackRequestWithCustomParams =
            CacheTrackRequestWithCustomParams(trackRequestRepository, customParamRepository)
    }

    private fun freshParams(): Params {
        val trackRequestCopy = trackRequest.copy()
        return Params(trackRequestCopy, cacheTrackRequestWithCustomParamsParams.trackingParams.toMap())
    }

    @Test
    fun `returns composed DataTrack when repositories succeed`() = runTest {
        val params = freshParams()
        val expectedCustomParams: List<CustomParam> =
            params.trackingParams.toCustomParams(params.trackRequest.id)
        val expected = Result.success(DataTrack(params.trackRequest, expectedCustomParams))

        coEvery { trackRequestRepository.addTrackRequest(params.trackRequest) } returns Result.success(params.trackRequest)
        coEvery { customParamRepository.addCustomParams(expectedCustomParams) } returns Result.success(expectedCustomParams)

        val result = cacheTrackRequestWithCustomParams(params)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expected.getOrNull())
        coVerify(exactly = 1) { trackRequestRepository.addTrackRequest(params.trackRequest) }
        coVerify(exactly = 1) { customParamRepository.addCustomParams(expectedCustomParams) }
    }

    @Test
    fun `propagates failure when caching track request fails`() = runTest {
        val params = freshParams()
        val failure = Result.failure<TrackRequest>(IOException("error"))

        coEvery { trackRequestRepository.addTrackRequest(params.trackRequest) } returns failure

        val result = cacheTrackRequestWithCustomParams(params)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(failure.exceptionOrNull())
        coVerify(exactly = 1) { trackRequestRepository.addTrackRequest(params.trackRequest) }
        coVerify(exactly = 0) { customParamRepository.addCustomParams(any()) }
    }

    @Test
    fun `propagates failure when caching custom params fails`() = runTest {
        val params = freshParams()
        val expectedCustomParams = params.trackingParams.toCustomParams(params.trackRequest.id)
        val error = IOException("error")

        coEvery { trackRequestRepository.addTrackRequest(params.trackRequest) } returns Result.success(params.trackRequest)
        coEvery { customParamRepository.addCustomParams(expectedCustomParams) } returns Result.failure(error)

        val result = cacheTrackRequestWithCustomParams(params)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(error)
        coVerify(exactly = 1) { trackRequestRepository.addTrackRequest(params.trackRequest) }
        coVerify(exactly = 1) { customParamRepository.addCustomParams(expectedCustomParams) }
    }
}
