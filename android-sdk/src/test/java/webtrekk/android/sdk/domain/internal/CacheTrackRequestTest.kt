package webtrekk.android.sdk.domain.internal

import io.mockk.coEvery
import io.mockk.mockkClass
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineContext
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import kotlin.coroutines.CoroutineContext

internal class CacheTrackRequestTest : CoroutineScope {

    private lateinit var trackRequestRepository: TrackRequestRepository
    private lateinit var cacheTrackRequest: CacheTrackRequest

    private var trackRequest = TrackRequest(name = "test", fns = "1", one = "1")

    private val job = SupervisorJob()
    private val testCoroutineContext = TestCoroutineContext()
    override val coroutineContext: CoroutineContext
        get() = job + testCoroutineContext

    @Before
    fun tearUp() {
        trackRequestRepository = mockkClass(TrackRequestRepository::class)

        cacheTrackRequest = CacheTrackRequest(trackRequestRepository, coroutineContext)
    }

    @After
    fun tearDown() {
        coroutineContext.cancel()
    }

    @Test
    fun `cache a new track request and return success`() {
        coEvery { trackRequestRepository.addTrackRequest(trackRequest) } returns Result.success(
            trackRequest
        )

        launch {
            val result = cacheTrackRequest(trackRequest).await()

            assertThat(Result.success(trackRequest), `is`(result))
        }
    }
}
