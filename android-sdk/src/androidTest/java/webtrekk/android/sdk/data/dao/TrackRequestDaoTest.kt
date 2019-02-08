package webtrekk.android.sdk.data.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.data.*

@RunWith(AndroidJUnit4::class)
internal class TrackRequestDaoTest : DbTest() {

    @Test
    @Throws(Exception::class)
    fun getSingleTrackRequest() = runBlocking {
        trackRequestDao.setTrackRequest(trackRequests[0])

        assertThat(trackRequestDao.getTrackRequests()[0].trackRequest, `is`(trackRequests[0]))
    }

    @Test
    @Throws(Exception::class)
    fun getTrackRequestsAndTheirCustomParams() = runBlocking {
        trackRequestDao.setTrackRequests(trackRequests)
        customParamDao.setCustomParams(customParams)

        assertThat(trackRequestDao.getTrackRequests(), `is`(dataTracks))
    }

    @Test
    @Throws(Exception::class)
    fun clearTrackRequests() = runBlocking {
        trackRequestDao.setTrackRequests(trackRequests)
        trackRequestDao.clearTrackRequests(listOf(trackRequests[0], trackRequests[1]))

        assertThat(trackRequestDao.getTrackRequests().size, `is`(trackRequests.size - 2))

        trackRequestDao.clearTrackRequests(trackRequests)

        assertThat(trackRequestDao.getTrackRequests().size, `is`(0))
    }
}
