package webtrekk.android.sdk.domain.external

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.util.coroutinesDispatchersProvider
import webtrekk.android.sdk.util.dataTrack
import webtrekk.android.sdk.util.trackRequest

/**
 * Tests for TrackException — validates the merged nested if:
 *   if (exception.cause != null && exception.cause?.message != null)
 * behaves identically to the original two-level if.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class TrackExceptionLogicTest : BaseExternalTest() {

    @MockK
    lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    private lateinit var trackException: TrackException

    @Before
    override fun setup() {
        super.setup()
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkObject(AppModule)
        io.mockk.every { AppModule.logger } returns io.mockk.mockk(relaxed = true)
        trackException = TrackException(coroutineContext, cacheTrackRequestWithCustomParams)
    }

    @After
    override fun tearDown() {
        super.tearDown()
        unmockkAll()
    }

    @Test
    fun `does not cache when opt-out is active`() = runTest {
        val params = TrackException.Params(
            trackRequest = trackRequest,
            isOptOut = true,
            exception = RuntimeException("boom"),
            exceptionType = ExceptionType.CAUGHT
        )
        trackException(params, coroutinesDispatchersProvider())
        coVerify(exactly = 0) { cacheTrackRequestWithCustomParams.invoke(any()) }
    }

    @Test
    fun `caches when opt-out is inactive`() = runTest {
        val params = TrackException.Params(
            trackRequest = trackRequest,
            isOptOut = false,
            exception = RuntimeException("boom"),
            exceptionType = ExceptionType.CAUGHT
        )
        trackException(params, coroutinesDispatchersProvider())
        coVerify(exactly = 1) { cacheTrackRequestWithCustomParams.invoke(any()) }
    }

    // ── merged-if logic: cause != null && cause.message != null ─────────────

    @Test
    fun `CAUGHT exception with cause and cause message — cause message IS included`() = runTest {
        val cause = RuntimeException("root cause message")
        val ex = RuntimeException("top", cause)

        val slot = slot<CacheTrackRequestWithCustomParams.Params>()
        coEvery { cacheTrackRequestWithCustomParams.invoke(capture(slot)) } returns Result.success(dataTrack)

        val params = TrackException.Params(
            trackRequest = trackRequest,
            isOptOut = false,
            exception = ex,
            exceptionType = ExceptionType.CAUGHT
        )
        trackException(params, coroutinesDispatchersProvider())

        assertThat(slot.captured.trackingParams[UrlParams.CRASH_CAUSE_MESSAGE]).isEqualTo("root cause message")
    }

    @Test
    fun `CAUGHT exception with cause but null cause message — cause message NOT included`() = runTest {
        // Cause has no message → merged condition (cause != null && cause.message != null) is false
        val cause = RuntimeException(null as String?)
        val ex = RuntimeException("top", cause)

        val slot = slot<CacheTrackRequestWithCustomParams.Params>()
        coEvery { cacheTrackRequestWithCustomParams.invoke(capture(slot)) } returns Result.success(dataTrack)

        val params = TrackException.Params(
            trackRequest = trackRequest,
            isOptOut = false,
            exception = ex,
            exceptionType = ExceptionType.CAUGHT
        )
        trackException(params, coroutinesDispatchersProvider())

        assertThat(slot.captured.trackingParams.containsKey(UrlParams.CRASH_CAUSE_MESSAGE)).isFalse()
    }

    @Test
    fun `CAUGHT exception with no cause — cause message and cause stack NOT included`() = runTest {
        val ex = RuntimeException("no cause")

        val slot = slot<CacheTrackRequestWithCustomParams.Params>()
        coEvery { cacheTrackRequestWithCustomParams.invoke(capture(slot)) } returns Result.success(dataTrack)

        val params = TrackException.Params(
            trackRequest = trackRequest,
            isOptOut = false,
            exception = ex,
            exceptionType = ExceptionType.CAUGHT
        )
        trackException(params, coroutinesDispatchersProvider())

        val captured = slot.captured.trackingParams
        assertThat(captured.containsKey(UrlParams.CRASH_CAUSE_MESSAGE)).isFalse()
        assertThat(captured.containsKey(UrlParams.CRASH_CAUSE_STACK)).isFalse()
    }

    @Test
    fun `CAUGHT exception always includes crash name and stack`() = runTest {
        val ex = RuntimeException("details")

        val slot = slot<CacheTrackRequestWithCustomParams.Params>()
        coEvery { cacheTrackRequestWithCustomParams.invoke(capture(slot)) } returns Result.success(dataTrack)

        val params = TrackException.Params(
            trackRequest = trackRequest,
            isOptOut = false,
            exception = ex,
            exceptionType = ExceptionType.CAUGHT
        )
        trackException(params, coroutinesDispatchersProvider())

        val captured = slot.captured.trackingParams
        assertThat(captured.containsKey(UrlParams.CRASH_NAME)).isTrue()
        assertThat(captured.containsKey(UrlParams.CRASH_STACK)).isTrue()
        assertThat(captured[UrlParams.CRASH_MESSAGE]).isEqualTo("details")
    }
}
