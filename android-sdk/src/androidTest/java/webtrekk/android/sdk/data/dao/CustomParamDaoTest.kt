package webtrekk.android.sdk.data.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.data.DbTest
import webtrekk.android.sdk.data.customParams
import webtrekk.android.sdk.data.trackRequests

@RunWith(AndroidJUnit4::class)
internal class CustomParamDaoTest : DbTest() {

    @Test
    @Throws(Exception::class)
    fun getCustomParams() = runBlocking {
        trackRequestDao.setTrackRequests(trackRequests)
        customParamDao.setCustomParams(customParams)

        assertThat(customParamDao.getCustomParams(), `is`(customParams))
    }

    @Test
    @Throws(Exception::class)
    fun getCustomParamsByTrackId() = runBlocking {
        trackRequestDao.setTrackRequests(trackRequests)
        customParamDao.setCustomParams(customParams)

        val customParamsById = customParamDao.getCustomParamsByTrackId(trackRequests[0].id)
        val filteredCustomParams = customParams.filter { it.trackId == trackRequests[0].id }

        assertThat(customParamsById, `is`(filteredCustomParams))
    }

    @Test
    @Throws(Exception::class)
    fun clearCustomParamsWhenTrackIsDeleted() = runBlocking {
        trackRequestDao.setTrackRequests(trackRequests)
        customParamDao.setCustomParams(customParams)

        trackRequestDao.clearTrackRequests(listOf(trackRequests[0]))
        val filteredCustomParams = customParams.filter { it.trackId != trackRequests[0].id }

        assertThat(customParamDao.getCustomParams(), `is`(filteredCustomParams))

        trackRequestDao.clearTrackRequests(trackRequests)

        assertThat(customParamDao.getCustomParams().size, `is`(0))
    }
}
