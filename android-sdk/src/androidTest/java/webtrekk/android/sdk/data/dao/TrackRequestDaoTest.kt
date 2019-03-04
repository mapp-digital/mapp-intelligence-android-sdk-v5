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
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.data.*
import webtrekk.android.sdk.data.entity.TrackRequest

@RunWith(AndroidJUnit4::class)
internal class TrackRequestDaoTest : DbTest() {

    @Test
    @Throws(Exception::class)
    fun getSingleTrackRequest() = runBlocking {
        assertThat(trackRequestDao.getTrackRequests()[0].trackRequest, `is`(trackRequests[0]))
    }

    @Test
    @Throws(Exception::class)
    fun getTrackRequests_WithCustomParams() = runBlocking {
        assertThat(trackRequestDao.getTrackRequests(), `is`(dataTracks))
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

        assertThat(results, `is`(updatedTrackRequests))
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
        assertThat(
            results,
            `is`(updatedTrackRequests.filter { it.requestState == TrackRequest.RequestState.DONE })
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
        assertThat(twoStateResults, `is`(updatedTrackRequests.filter {
            it.requestState == TrackRequest.RequestState.DONE ||
                it.requestState == TrackRequest.RequestState.FAILED
        }))
    }

    @Test
    @Throws(Exception::class)
    fun clearTrackRequests() = runBlocking {
        // First, let's delete only 2 elements and verify their results
        trackRequestDao.clearTrackRequests(listOf(trackRequests[0], trackRequests[1]))

        assertThat(trackRequestDao.getTrackRequests().size, `is`(trackRequests.size - 2))

        // Clear all track requests and verify
        trackRequestDao.clearTrackRequests(trackRequests)

        assertThat(trackRequestDao.getTrackRequests().size, `is`(0))
    }
}
