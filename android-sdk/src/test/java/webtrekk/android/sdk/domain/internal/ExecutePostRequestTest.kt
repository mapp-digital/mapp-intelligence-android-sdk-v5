package webtrekk.android.sdk.domain.internal

import buildUrlRequestForTesting
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.mockkClass
import webtrekk.android.sdk.api.datasource.SyncRequestsDataSourceImpl
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.util.dataTrack

/**
 * Created by Aleksandar Marinkovic on 16/07/2020.
 * Copyright (c) 2020 MAPP.
 */
internal class ExecutePostRequestTest : StringSpec({

    val trackRequestRepository = mockkClass(TrackRequestRepository::class)
    val syncRequestDataSource = mockkClass(SyncRequestsDataSourceImpl::class)
    val executeRequest =
        ExecuteRequest(trackRequestRepository, syncRequestDataSource)

    val urlRequest = dataTrack.buildUrlRequestForTesting("https://www.webtrekk.com", listOf("123"))
    val params = ExecuteRequest.Params(request = urlRequest, dataTrack = dataTrack)

    "execute the request then update its track request's state" {
        val resultSuccess = Result.success(dataTrack)

        coEvery {
            syncRequestDataSource.sendRequest(urlRequest, dataTrack)
        } returns resultSuccess

        val syncRequestSuccess = syncRequestDataSource.sendRequest(urlRequest, dataTrack)

        if (syncRequestSuccess.isSuccess) {
            val updatedTrackRequest = syncRequestSuccess.getOrThrow().trackRequest
            updatedTrackRequest.requestState = TrackRequest.RequestState.DONE

            val updatedTrackRequestSuccess = Result.success(listOf(updatedTrackRequest))

            coEvery {
                trackRequestRepository.updateTrackRequests(updatedTrackRequest)
            } returns updatedTrackRequestSuccess

            executeRequest(params) shouldBe (resultSuccess)
        }
    }
})