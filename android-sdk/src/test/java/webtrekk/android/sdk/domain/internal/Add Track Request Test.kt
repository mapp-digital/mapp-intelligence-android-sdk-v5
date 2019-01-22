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
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.model.TrackRequest
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import kotlin.coroutines.CoroutineContext

internal class `Add Track Request Test` : CoroutineScope {

    lateinit var trackRequestRepository: TrackRequestRepository
    lateinit var trackRequest: TrackRequest
    lateinit var addTrackRequest: AddTrackRequest

    private val job = SupervisorJob()
    private val testCoroutineContext = TestCoroutineContext()
    override val coroutineContext: CoroutineContext
        get() = job + testCoroutineContext

    @Before
    fun tearUp() {
        trackRequestRepository = mockkClass(TrackRequestRepository::class)
        trackRequest = TrackRequest(name = "test")

        addTrackRequest = AddTrackRequest(trackRequestRepository, coroutineContext)
    }

    @After
    fun tearDown() {
        coroutineContext.cancel()
    }

    @Test
    fun `insert track request to the database and return success`() {
        coEvery { trackRequestRepository.addTrackRequest(trackRequest) } returns DataResult.Success(
            trackRequest
        )

        launch {
            val result = addTrackRequest(trackRequest).await()

            assertThat(DataResult.Success(trackRequest).data, `is`(result))
        }
    }
}
