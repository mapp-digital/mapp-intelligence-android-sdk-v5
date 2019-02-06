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
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import kotlin.coroutines.CoroutineContext

internal class GetCachedTrackRequestsTest : CoroutineScope {

    private lateinit var trackRequestRepository: TrackRequestRepository
    private lateinit var getCachedTrackRequests: GetCachedTrackRequests

    private val dataTracks = listOf(
        DataTrack(
            trackRequest = TrackRequest(name = "page 1", fns = "1", one = "1").apply { id = 1 },
            customParams = listOf(CustomParam(trackId = 1, paramKey = "cs", paramValue = "val 1"),
                CustomParam(trackId = 1, paramKey = "cd", paramValue = "val 2"))
        ),
        DataTrack(
            trackRequest = TrackRequest(name = "page 2", fns = "0", one = "0").apply { this.id = 2 },
            customParams = listOf(CustomParam(trackId = 2, paramKey = "cs", paramValue = "val 3"))
        ),
        DataTrack(
            trackRequest = TrackRequest(name = "page 3", fns = "0", one = "0").apply { this.id = 3 },
            customParams = emptyList()
        ),
        DataTrack(
            trackRequest = TrackRequest(name = "page 4", fns = "0", one = "0").apply { this.id = 4 },
            customParams = emptyList()
        )
    )

    private val job = SupervisorJob()
    private val testCoroutineContext = TestCoroutineContext()
    override val coroutineContext: CoroutineContext
        get() = job + testCoroutineContext

    @Before
    fun tearUp() {
        trackRequestRepository = mockkClass(TrackRequestRepository::class)

        getCachedTrackRequests =
            GetCachedTrackRequests(trackRequestRepository, testCoroutineContext)
    }

    @After
    fun tearDown() {
        testCoroutineContext.cancel()
    }

    @Test
    fun `get all tracks and their custom params`() {
        coEvery { trackRequestRepository.getTrackRequests() } returns Result.success(dataTracks)

        launch {
            val result = getCachedTrackRequests().await()

            assertThat(Result.success(dataTracks), equalTo(result))
        }
    }
}
