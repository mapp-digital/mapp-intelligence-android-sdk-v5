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
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.util.dataTracks
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class GetCachedDataTracksTest {

    @MockK
    lateinit var trackRequestRepository: TrackRequestRepository

    private lateinit var getCachedDataTracks: GetCachedDataTracks

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getCachedDataTracks = GetCachedDataTracks(trackRequestRepository)
    }

    private fun copyDataTracks(): List<DataTrack> {
        return dataTracks.map { dataTrack ->
            val trackRequestCopy = dataTrack.trackRequest.copy()
            val customParamsCopy = dataTrack.customParams.map(CustomParam::copy)
            DataTrack(trackRequestCopy, customParamsCopy)
        }
    }

    @Test
    fun `returns all tracks when no states are provided`() = runTest {
        val expectedTracks = copyDataTracks()
        val expectedResult = Result.success(expectedTracks)
        coEvery { trackRequestRepository.getTrackRequests() } returns expectedResult

        val result = getCachedDataTracks(GetCachedDataTracks.Params(emptyList()))

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedTracks)
        coVerify(exactly = 1) { trackRequestRepository.getTrackRequests() }
        coVerify(exactly = 0) { trackRequestRepository.getTrackRequestsByState(any()) }
    }

    @Test
    fun `returns filtered tracks for provided states`() = runTest {
        val requestStates = listOf(TrackRequest.RequestState.NEW, TrackRequest.RequestState.FAILED)
        val filteredTracks = copyDataTracks().filter { it.trackRequest.requestState in requestStates }
        val expectedResult = Result.success(filteredTracks)

        coEvery { trackRequestRepository.getTrackRequestsByState(requestStates) } returns expectedResult

        val result = getCachedDataTracks(GetCachedDataTracks.Params(requestStates))

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(filteredTracks)
        coVerify(exactly = 1) { trackRequestRepository.getTrackRequestsByState(requestStates) }
        coVerify(exactly = 0) { trackRequestRepository.getTrackRequests() }
    }

    @Test
    fun `propagates repository failures`() = runTest {
        val error = IOException("error")
        val failure = Result.failure<List<DataTrack>>(error)
        coEvery { trackRequestRepository.getTrackRequests() } returns failure

        val result = getCachedDataTracks(GetCachedDataTracks.Params(emptyList()))

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(error)
        coVerify(exactly = 1) { trackRequestRepository.getTrackRequests() }
    }
}
