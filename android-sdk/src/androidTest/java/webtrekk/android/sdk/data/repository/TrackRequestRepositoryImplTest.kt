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

package webtrekk.android.sdk.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.data.*
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

@RunWith(AndroidJUnit4::class)
internal class TrackRequestRepositoryImplTest : DbTest() {

    private lateinit var trackRequestRepositoryImpl: TrackRequestRepositoryImpl

    @Before
    override fun setUp() {
        super.setUp()

        trackRequestRepositoryImpl = TrackRequestRepositoryImpl(trackRequestDao)
    }

    @Test
    @Throws(Exception::class)
    fun addTrackRequest() = runBlocking {
        val result = trackRequestRepositoryImpl.addTrackRequest(trackRequests[0])

        assertThat(result, `is`(Result.success(trackRequests[0])))
    }

    @Test
    @Throws(Exception::class)
    fun getTrackRequests() = runBlocking {
        val result = trackRequestRepositoryImpl.getTrackRequests()

        assertThat(result, `is`(Result.success(dataTracks)))
    }

    @Test
    @Throws(Exception::class)
    fun updateTrackRequests() = runBlocking {
        val updatedTrackRequests = trackRequests
        updatedTrackRequests[0].requestState = TrackRequest.RequestState.FAILED
        updatedTrackRequests[1].requestState = TrackRequest.RequestState.FAILED
        updatedTrackRequests[2].requestState = TrackRequest.RequestState.DONE

        // Update the updated track requests in DB
        trackRequestRepositoryImpl.updateTrackRequests(*updatedTrackRequests.toTypedArray())

        val results = trackRequestDao.getTrackRequests().map { it.trackRequest }

        // Verify that the updated track requests are the same
        assertThat(results, `is`(updatedTrackRequests))
    }

    @Test
    @Throws(Exception::class)
    fun getTrackRequests_ByState() = runBlocking {
        val updatedTrackRequests = trackRequests
        updatedTrackRequests[0].requestState = TrackRequest.RequestState.FAILED
        updatedTrackRequests[1].requestState = TrackRequest.RequestState.FAILED
        updatedTrackRequests[2].requestState = TrackRequest.RequestState.DONE

        val updatedDataTracks = listOf(
            DataTrack(
                trackRequest = updatedTrackRequests[0],
                customParams = listOf(customParams[0], customParams[1])
            ),
            DataTrack(
                trackRequest = updatedTrackRequests[1],
                customParams = listOf(customParams[2])
            ),
            DataTrack(trackRequest = updatedTrackRequests[2], customParams = emptyList())
        )

        trackRequestRepositoryImpl.updateTrackRequests(*updatedTrackRequests.toTypedArray())

        val results = trackRequestRepositoryImpl.getTrackRequestsByState(
            listOf(
                TrackRequest.RequestState.DONE,
                TrackRequest.RequestState.FAILED
            )
        )

        assertThat(results, `is`(Result.success(updatedDataTracks)))
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllTrackRequests() = runBlocking {
        val result = trackRequestRepositoryImpl.deleteTrackRequests(trackRequests)

        assertThat(result, `is`(Result.success(true)))
    }

    @Test
    @Throws(Exception::class)
    fun deleteTrackRequests() = runBlocking {
        val deletedTrackRequests = listOf(trackRequests[0], trackRequests[1])

        trackRequestRepositoryImpl.deleteTrackRequests(deletedTrackRequests)

        val trackRequestsAfterDelete = listOf(
            DataTrack(trackRequests[2], emptyList()),
            DataTrack(trackRequests[3], emptyList())
        )

        val result = trackRequestRepositoryImpl.getTrackRequests()

        assertThat(result, `is`(Result.success(trackRequestsAfterDelete)))
    }
}
