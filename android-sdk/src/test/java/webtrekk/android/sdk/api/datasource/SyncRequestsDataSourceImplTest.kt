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
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncRequestsDataSourceImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var dataSource: SyncRequestsDataSourceImpl

    private val okHttpClient = OkHttpClient()

    private val dataTrack = DataTrack(
        trackRequest = TrackRequest(
            name = "page",
            forceNewSession = "0",
            appFirstOpen = "0",
            everId = "123"
        ),
        customParams = emptyList()
    )

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        dataSource = SyncRequestsDataSourceImpl(okHttpClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `returns success when server responds 200`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("ok"))
        val request = mockWebServer.url("/track").let {
            okhttp3.Request.Builder().url(it).build()
        }

        val result = dataSource.sendRequest(request, dataTrack)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(dataTrack)
    }

    @Test
    fun `returns failure when server responds non 2xx`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("boom"))
        val request = mockWebServer.url("/track").let {
            okhttp3.Request.Builder().url(it).build()
        }

        val result = dataSource.sendRequest(request, dataTrack)

        assertThat(result.isFailure).isTrue()
    }
}
