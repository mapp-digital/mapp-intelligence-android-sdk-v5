package webtrekk.android.sdk.api.datasource

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncPostRequestsDataSourceImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var dataSource: SyncPostRequestsDataSourceImpl

    private val okHttpClient = OkHttpClient()

    private val dataTracks = listOf(
        DataTrack(
            trackRequest = TrackRequest(
                name = "page 1",
                forceNewSession = "0",
                appFirstOpen = "0",
                everId = "1"
            ),
            customParams = listOf(CustomParam(trackId = 1, paramKey = "k", paramValue = "v"))
        )
    )

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        dataSource = SyncPostRequestsDataSourceImpl(okHttpClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `returns success on 200 response`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("ok"))
        val request = mockWebServer.url("/batch").let {
            okhttp3.Request.Builder().url(it).build()
        }

        val result = dataSource.sendRequest(request, dataTracks)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(dataTracks)
    }

    @Test
    fun `returns failure on error response`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))
        val request = mockWebServer.url("/batch").let {
            okhttp3.Request.Builder().url(it).build()
        }

        val result = dataSource.sendRequest(request, dataTracks)

        assertThat(result.isFailure).isTrue()
    }
}
