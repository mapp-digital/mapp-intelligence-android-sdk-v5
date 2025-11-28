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
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.util.trackRequests
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class ClearTrackRequestsTest {

    @MockK
    lateinit var trackRequestRepository: TrackRequestRepository

    private lateinit var clearTrackRequests: ClearTrackRequests

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearTrackRequests = ClearTrackRequests(trackRequestRepository)
    }

    @Test
    fun `clears all track requests when params are empty`() = runTest {
        coEvery { trackRequestRepository.deleteAllTrackRequests() } returns Result.success(true)

        val result = clearTrackRequests(ClearTrackRequests.Params(emptyList()))

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
        coVerify(exactly = 1) { trackRequestRepository.deleteAllTrackRequests() }
        coVerify(exactly = 0) { trackRequestRepository.deleteTrackRequests(any()) }
    }

    @Test
    fun `clears only provided track requests when list is present`() = runTest {
        coEvery { trackRequestRepository.deleteTrackRequests(trackRequests) } returns Result.success(true)

        val result = clearTrackRequests(ClearTrackRequests.Params(trackRequests))

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
        coVerify(exactly = 1) { trackRequestRepository.deleteTrackRequests(trackRequests) }
        coVerify(exactly = 0) { trackRequestRepository.deleteAllTrackRequests() }
    }

    @Test
    fun `propagates failure from repository`() = runTest {
        val error = IOException("error")
        coEvery { trackRequestRepository.deleteAllTrackRequests() } returns Result.failure(error)

        val result = clearTrackRequests(ClearTrackRequests.Params(emptyList()))

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(error)
        coVerify(exactly = 1) { trackRequestRepository.deleteAllTrackRequests() }
    }
}
