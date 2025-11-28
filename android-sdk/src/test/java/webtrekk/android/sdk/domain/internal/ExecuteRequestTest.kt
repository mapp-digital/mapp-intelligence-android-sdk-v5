package webtrekk.android.sdk.domain.internal

import buildUrlRequestForTesting
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.api.datasource.SyncRequestsDataSourceImpl
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.util.dataTrack

@OptIn(ExperimentalCoroutinesApi::class)
internal class ExecuteRequestTest {

    @MockK
    lateinit var trackRequestRepository: TrackRequestRepository

    @MockK
    lateinit var syncRequestDataSource: SyncRequestsDataSourceImpl

    private lateinit var executeRequest: ExecuteRequest

    private val urlRequest = dataTrack.buildUrlRequestForTesting("https://www.webtrekk.com", listOf("123"))

    private fun freshParams(): ExecuteRequest.Params {
        val copiedTrack = dataTrack.copy(
            trackRequest = dataTrack.trackRequest.copy(),
            customParams = dataTrack.customParams.map { it.copy() }
        )
        return ExecuteRequest.Params(request = urlRequest, dataTrack = copiedTrack)
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        executeRequest = ExecuteRequest(trackRequestRepository, syncRequestDataSource)
    }

    @Test
    fun `updates track request state to DONE when send succeeds`() = runTest {
        val params = freshParams()
        val resultSuccess = Result.success(params.dataTrack)
        coEvery { syncRequestDataSource.sendRequest(urlRequest, params.dataTrack) } returns resultSuccess
        coEvery { trackRequestRepository.updateTrackRequests(any<TrackRequest>()) } returns Result.success(listOf(params.dataTrack.trackRequest))

        val result = executeRequest(params)

        assertThat(result.isSuccess).isTrue()
        assertThat(params.dataTrack.trackRequest.requestState).isEqualTo(TrackRequest.RequestState.DONE)
        coVerify(exactly = 1) { syncRequestDataSource.sendRequest(urlRequest, params.dataTrack) }
        coVerify(exactly = 1) { trackRequestRepository.updateTrackRequests(params.dataTrack.trackRequest) }
    }

    @Test
    fun `updates track request state to FAILED when send fails`() = runTest {
        val params = freshParams()
        val failure = Result.failure<webtrekk.android.sdk.data.entity.DataTrack>(RuntimeException("boom"))
        coEvery { syncRequestDataSource.sendRequest(urlRequest, params.dataTrack) } returns failure
        coEvery { trackRequestRepository.updateTrackRequests(params.dataTrack.trackRequest) } returns Result.success(listOf(params.dataTrack.trackRequest))

        val result = executeRequest(params)

        assertThat(result.isFailure).isTrue()
        assertThat(params.dataTrack.trackRequest.requestState).isEqualTo(TrackRequest.RequestState.FAILED)
        coVerify(exactly = 1) { trackRequestRepository.updateTrackRequests(params.dataTrack.trackRequest) }
    }
}
