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
import webtrekk.android.sdk.api.datasource.SyncPostRequestsDataSource
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.util.dataTrack
import webtrekk.android.sdk.util.dataTracks

/**
 * Created by Aleksandar Marinkovic on 16/07/2020.
 * Copyright (c) 2020 MAPP.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class ExecutePostRequestTest {

    @MockK
    lateinit var trackRequestRepository: TrackRequestRepository

    @MockK
    lateinit var syncRequestDataSource: SyncPostRequestsDataSource<List<webtrekk.android.sdk.data.entity.DataTrack>>

    private lateinit var executePostRequest: ExecutePostRequest

    private val urlRequest = dataTrack.buildUrlRequestForTesting("https://www.webtrekk.com", listOf("123"))

    private fun freshParams(): ExecutePostRequest.Params {
        val copiedTracks = dataTracks.map { original ->
            original.copy(
                trackRequest = original.trackRequest.copy(),
                customParams = original.customParams.map { it.copy() }
            )
        }
        return ExecutePostRequest.Params(request = urlRequest, dataTracks = copiedTracks)
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        executePostRequest = ExecutePostRequest(trackRequestRepository, syncRequestDataSource)
    }

    @Test
    fun `marks requests as done when sending succeeds`() = runTest {
        val params = freshParams()
        val resultSuccess = Result.success(params.dataTracks)
        coEvery { syncRequestDataSource.sendRequest(urlRequest, params.dataTracks) } returns resultSuccess
        coEvery { trackRequestRepository.updateTrackRequests(any<TrackRequest>()) } returns Result.success(listOf(dataTrack.trackRequest))

        val result = executePostRequest(params)

        assertThat(result.isSuccess).isTrue()
        params.dataTracks.forEach {
            assertThat(it.trackRequest.requestState).isEqualTo(TrackRequest.RequestState.DONE)
        }
        coVerify(exactly = params.dataTracks.size) { trackRequestRepository.updateTrackRequests(any<TrackRequest>()) }
    }

    @Test
    fun `marks requests as failed when sending fails`() = runTest {
        val params = freshParams()
        val failure = Result.failure<List<webtrekk.android.sdk.data.entity.DataTrack>>(RuntimeException("boom"))
        coEvery { syncRequestDataSource.sendRequest(urlRequest, params.dataTracks) } returns failure
        coEvery { trackRequestRepository.updateTrackRequests(any<TrackRequest>()) } returns Result.success(listOf(dataTrack.trackRequest))

        val result = executePostRequest(params)

        assertThat(result.isFailure).isTrue()
        params.dataTracks.forEach {
            assertThat(it.trackRequest.requestState).isEqualTo(TrackRequest.RequestState.FAILED)
        }
        coVerify(exactly = params.dataTracks.size) { trackRequestRepository.updateTrackRequests(any<TrackRequest>()) }
    }
}
