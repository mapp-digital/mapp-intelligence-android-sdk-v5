package webtrekk.android.sdk.domain.internal

import io.mockk.coEvery
import io.mockk.mockkClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineContext
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import kotlin.coroutines.CoroutineContext

internal class CacheTrackRequestWithCustomParamsTest : CoroutineScope {

    private lateinit var trackRequestRepository: TrackRequestRepository
    private lateinit var customParamRepository: CustomParamRepository
    private lateinit var cacheTrackRequest: CacheTrackRequest
    private lateinit var addCustomParams: AddCustomParams

    private var trackRequest = TrackRequest(name = "track request 1", fns = "1", one = "1").apply { id = 1 }

    private val job = Job()
    private val testCoroutineContext = TestCoroutineContext()
    override val coroutineContext: CoroutineContext
        get() = job + testCoroutineContext

    @Before
    fun tearUp() {
        trackRequestRepository = mockkClass(TrackRequestRepository::class)
        customParamRepository = mockkClass(CustomParamRepository::class)

        cacheTrackRequest = CacheTrackRequest(trackRequestRepository, coroutineContext)
        addCustomParams = AddCustomParams(customParamRepository, coroutineContext)
    }

    @After
    fun tearDown() {
        coroutineContext.cancel()
    }

    @Test
    fun `cache track request then append its custom params`() {
        coEvery { trackRequestRepository.addTrackRequest(trackRequest) } returns Result.success(
            trackRequest
        )

        launch {
            val trackRequestResult = cacheTrackRequest(trackRequest).await() as TrackRequest

            assertThat(trackRequest, `is`(trackRequestResult))

            val customParams = listOf(
                CustomParam(trackId = trackRequestResult.id, paramKey = "cs", paramValue = "val 1"),
                CustomParam(trackId = trackRequestResult.id, paramKey = "cd", paramValue = "val 2")
            )

            coEvery { customParamRepository.addCustomParams(customParams) } returns Result.success(
                customParams
            )

            addCustomParams(customParams)

            assertThat(Result.success(customParams), `is`(addCustomParams.testResult))
        }
    }
}
