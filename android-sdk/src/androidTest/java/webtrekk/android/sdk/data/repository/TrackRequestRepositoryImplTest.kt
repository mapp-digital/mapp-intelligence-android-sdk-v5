package webtrekk.android.sdk.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.data.*

@RunWith(AndroidJUnit4::class)
internal class TrackRequestRepositoryImplTest : DbTest() {

    private lateinit var trackRequestRepositoryImpl: TrackRequestRepositoryImpl

    @Before
    fun setUp() {
        trackRequestRepositoryImpl = TrackRequestRepositoryImpl(trackRequestDao)
    }

    @Test
    @Throws(Exception::class)
    fun addTrackRequest() = runBlocking {
        val result = trackRequestRepositoryImpl.addTrackRequest(trackRequests[0])

        assertThat(Result.success(trackRequests[0]), `is`(result))
    }

    @Test
    @Throws(Exception::class)
    fun getTrackRequests() = runBlocking {
        trackRequestDao.setTrackRequests(trackRequests)
        customParamDao.setCustomParams(customParams)

        val result = trackRequestRepositoryImpl.getTrackRequests()

        assertThat(Result.success(dataTracks), `is`(result))
    }

    @Test
    @Throws(Exception::class)
    fun deleteTrackRequests() = runBlocking {
        trackRequestDao.setTrackRequests(trackRequests)

        val result = trackRequestRepositoryImpl.deleteTrackRequests(trackRequests)

        assertThat(Result.success(true), `is`(result))
    }
}
