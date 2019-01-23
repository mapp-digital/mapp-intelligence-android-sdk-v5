package webtrekk.android.sdk.domain.internal

import io.mockk.coEvery
import io.mockk.mockkClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineContext
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.model.CustomParam
import webtrekk.android.sdk.data.model.DataTrack
import webtrekk.android.sdk.data.model.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import kotlin.coroutines.CoroutineContext

internal class GetCachedTrackRequestsTest : CoroutineScope {

    private lateinit var trackRequestRepository: TrackRequestRepository
    private lateinit var getCachedTrackRequests: GetCachedTrackRequests

    private val dataTracks = listOf(
        DataTrack(
            trackRequest = TrackRequest(name = "page 1").apply { id = 1 },
            customParam = CustomParam(trackId = 1, paramKey = "cs", paramValue = "val 1")
        ),
        DataTrack(
            trackRequest = TrackRequest(name = "page 1").apply { id = 1 },
            customParam = CustomParam(trackId = 1, paramKey = "cd", paramValue = "val 2")
        ),
        DataTrack(
            trackRequest = TrackRequest(name = "page 2").apply { this.id = 2 },
            customParam = CustomParam(trackId = 2, paramKey = "cs", paramValue = "val 3")
        ),
        DataTrack(
            trackRequest = TrackRequest(name = "page 3").apply { this.id = 3 },
            customParam = null
        ),
        DataTrack(
            trackRequest = TrackRequest(name = "page 4").apply { this.id = 4 },
            customParam = null
        )
    )

    private val job = SupervisorJob()
    private val testCoroutineContext = TestCoroutineContext()
    override val coroutineContext: CoroutineContext
        get() = job + testCoroutineContext

    @Before
    fun tearUp() {
        trackRequestRepository = mockkClass(TrackRequestRepository::class)

        getCachedTrackRequests = GetCachedTrackRequests(trackRequestRepository, testCoroutineContext)
    }

    @After
    fun tearDown() {
        testCoroutineContext.cancel()
    }

    @Test
    fun `get all tracks and their custom params`() {
        coEvery { trackRequestRepository.getTrackRequests() } returns DataResult.Success(dataTracks)

        launch {
            val result = getCachedTrackRequests().await()

            assertThat(DataResult.Success(dataTracks), equalTo(result))
        }
    }
}
