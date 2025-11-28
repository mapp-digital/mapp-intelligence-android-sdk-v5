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
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.util.cacheTrackRequestParams
import webtrekk.android.sdk.util.trackRequest
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class CacheTrackRequestTest {

    @MockK
    lateinit var trackRequestRepository: TrackRequestRepository

    private lateinit var cacheTrackRequest: CacheTrackRequest

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        cacheTrackRequest = CacheTrackRequest(trackRequestRepository)
    }

    @Test
    fun `returns cached track when repository succeeds`() = runTest {
        val expected = Result.success(trackRequest)

        coEvery { trackRequestRepository.addTrackRequest(trackRequest) } returns expected

        val result = cacheTrackRequest(cacheTrackRequestParams)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(trackRequest)
        coVerify(exactly = 1) { trackRequestRepository.addTrackRequest(trackRequest) }
    }

    @Test
    fun `propagates failure from repository`() = runTest {
        val error = IOException("error")
        val expected = Result.failure<TrackRequest>(error)

        coEvery { trackRequestRepository.addTrackRequest(trackRequest) } returns expected

        val result = cacheTrackRequest(cacheTrackRequestParams)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(error)
        coVerify(exactly = 1) { trackRequestRepository.addTrackRequest(trackRequest) }
    }
}
