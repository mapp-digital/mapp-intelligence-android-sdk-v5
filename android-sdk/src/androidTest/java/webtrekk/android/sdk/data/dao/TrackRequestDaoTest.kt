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

package webtrekk.android.sdk.data.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.data.DbTest
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.dataTracks
import webtrekk.android.sdk.trackRequests

@RunWith(AndroidJUnit4::class)
internal class TrackRequestDaoTest : DbTest() {

    @Test
    fun insert_track_requests_Test() = runBlocking {
        val requests = ArrayList(trackRequests)
        trackRequestDao.setTrackRequests(requests)
        val dbRequests = trackRequestDao.getTrackRequests().map { it.trackRequest }
        MatcherAssert.assertThat(dbRequests, equalTo(requests))
    }

    @Test
    fun update_everId_for_all_requests_test() = runBlocking {
        val request = ArrayList(trackRequests)
        trackRequestDao.setTrackRequests(request)
        trackRequestDao.updateEverId("2222")
        val dbRequests = trackRequestDao.getTrackRequests().map { it.trackRequest }
        dbRequests.forEach {
            MatcherAssert.assertThat(it.everId, equalTo("2222"))
        }
    }

    @Test
    @Throws(Exception::class)
    fun getSingleTrackRequest() = runBlocking {
        MatcherAssert.assertThat(
            trackRequestDao.getTrackRequests()[0].trackRequest, equalTo(
                trackRequests[0]
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun getTrackRequests_WithCustomParams() = runBlocking {
        MatcherAssert.assertThat(trackRequestDao.getTrackRequests(), equalTo(dataTracks))
    }

    @Test
    @Throws(Exception::class)
    fun updateTrackRequests() = runBlocking {
        val updatedTrackRequests = trackRequests
        updatedTrackRequests[0].requestState = TrackRequest.RequestState.FAILED
        updatedTrackRequests[1].requestState = TrackRequest.RequestState.FAILED
        updatedTrackRequests[2].requestState = TrackRequest.RequestState.DONE

        trackRequestDao.updateTrackRequests(*updatedTrackRequests.toTypedArray())

        val results = trackRequestDao.getTrackRequests().map { it.trackRequest }

        MatcherAssert.assertThat(results, equalTo(updatedTrackRequests))
    }

    @Test
    @Throws(Exception::class)
    fun getTrackRequests_ByState() = runBlocking {
        val updatedTrackRequests = trackRequests
        updatedTrackRequests[0].requestState = TrackRequest.RequestState.DONE
        updatedTrackRequests[1].requestState = TrackRequest.RequestState.DONE
        updatedTrackRequests[2].requestState = TrackRequest.RequestState.FAILED

        // Update the track requests state
        trackRequestDao.updateTrackRequests(*updatedTrackRequests.toTypedArray())

        val results =
            trackRequestDao.getTrackRequestsByState(listOf(TrackRequest.RequestState.DONE.value))
                .map { it.trackRequest }

        // Validate that we have the updated track requests with state "DONE" from DB
        MatcherAssert.assertThat(
            results,
            equalTo(updatedTrackRequests.filter { it.requestState == TrackRequest.RequestState.DONE })
        )

        val twoStateResults =
            trackRequestDao.getTrackRequestsByState(
                listOf(
                    TrackRequest.RequestState.DONE.value,
                    TrackRequest.RequestState.FAILED.value
                )
            )
                .map { it.trackRequest }

        // Validate that if we had multi states, we get the updated ones from DB
        MatcherAssert.assertThat(twoStateResults, equalTo(updatedTrackRequests.filter {
            it.requestState == TrackRequest.RequestState.DONE ||
                    it.requestState == TrackRequest.RequestState.FAILED
        }))
    }

    @Test
    @Throws(Exception::class)
    fun clearTrackRequests() = runBlocking {
        // First, let's delete only 2 elements and verify their results
        trackRequestDao.clearTrackRequests(listOf(trackRequests[0], trackRequests[1]))

        MatcherAssert.assertThat(
            trackRequestDao.getTrackRequests().size,
            equalTo(trackRequests.size - 2)
        )

        // Clear all track requests and verify
        trackRequestDao.clearTrackRequests(trackRequests)

        MatcherAssert.assertThat(trackRequestDao.getTrackRequests().size, equalTo(0))
    }

    @Test
    @Throws(Exception::class)
    fun clear_all_requests_test() = runBlocking {
        val dbRequests = trackRequestDao.getTrackRequests().map { it.trackRequest }
        MatcherAssert.assertThat(dbRequests.size, equalTo(4))

        trackRequestDao.clearAllTrackRequests()
        val dbRequestsEmpty = trackRequestDao.getTrackRequests().map { it.trackRequest }
        MatcherAssert.assertThat(dbRequestsEmpty.size, equalTo(0))
    }
}
