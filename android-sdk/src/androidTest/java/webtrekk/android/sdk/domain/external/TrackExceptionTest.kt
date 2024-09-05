package webtrekk.android.sdk.domain.external

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.trackRequest
import webtrekk.android.sdk.util.CoroutineDispatchers

class TrackExceptionTest {

    private lateinit var trackException: TrackException

    private lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    private lateinit var trackRequestRepository: TrackRequestRepository

    private lateinit var customParamRepository: CustomParamRepository

    private lateinit var context: Context

    private lateinit var webtrekk: Webtrekk

    private lateinit var config: Config

    private lateinit var coroutineDispatchers: CoroutineDispatchers

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        config = mockk(relaxed = true)

        Webtrekk.getInstance().init(context, config)

        coroutineDispatchers = CoroutineDispatchers(
            Dispatchers.Unconfined,
            Dispatchers.Unconfined,
            Dispatchers.Unconfined
        )

        webtrekk = Webtrekk.getInstance()
        trackRequestRepository = TrackRequestRepositoryTest()
        customParamRepository = CustomParamsRepositoryTest()
        cacheTrackRequestWithCustomParams =
            CacheTrackRequestWithCustomParams(trackRequestRepository, customParamRepository)
        trackException =
            spyk(TrackException(Dispatchers.Unconfined, cacheTrackRequestWithCustomParams))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun trackExceptionWithCtParameter() = runBlocking {
        val invokeParams = TrackException.Params(
            trackRequest,
            false,
            RuntimeException("Test Runtime Exception"),
            ExceptionType.CAUGHT,
            context
        )

        trackException(invokeParams, coroutineDispatchers)

        verify(atLeast = 1) { trackException(any(), any()) }

        val data = trackRequestRepository.getTrackRequests().getOrNull()
        val customParams =
            customParamRepository.getCustomParamsByTrackId(trackRequest.id).getOrNull()

        Truth.assertThat(data).isNotNull()
        Truth.assertThat(customParams?.filter { it.paramKey == UrlParams.EVENT_NAME }).isNotEmpty()
    }
}