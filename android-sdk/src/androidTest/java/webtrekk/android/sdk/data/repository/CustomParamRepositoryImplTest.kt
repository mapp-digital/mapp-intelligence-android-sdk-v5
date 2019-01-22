package webtrekk.android.sdk.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.DbTest
import webtrekk.android.sdk.data.customParams
import webtrekk.android.sdk.data.trackRequests

@RunWith(AndroidJUnit4::class)
internal class CustomParamRepositoryImplTest : DbTest() {

    private lateinit var customParamRepositoryImpl: CustomParamRepositoryImpl

    @Before
    fun setUp() {
        customParamRepositoryImpl = CustomParamRepositoryImpl(customParamDao)
    }

    @Test
    @Throws(Exception::class)
    fun addCustomParams() = runBlocking {
        trackRequestDao.setTrackRequests(trackRequests)
        val result = customParamRepositoryImpl.addCustomParams(customParams)

        assertThat(DataResult.Success(customParams), `is`(result))
    }

    @Test
    @Throws
    fun getCustomParamById() = runBlocking {
        trackRequestDao.setTrackRequests(trackRequests)
        customParamDao.setCustomParams(customParams)

        val customParamsById =
            customParamRepositoryImpl.getCustomParamsByTrackId(trackRequests[0].id)
        val filteredCustomParams = customParams.filter { it.trackId == trackRequests[0].id }

        assertThat(DataResult.Success(filteredCustomParams), `is`(customParamsById))
    }
}