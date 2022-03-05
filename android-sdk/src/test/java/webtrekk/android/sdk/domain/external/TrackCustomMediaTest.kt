package webtrekk.android.sdk.domain.external

import io.mockk.Called
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.util.cacheTrackRequestWithCustomParamsParams
import webtrekk.android.sdk.util.coroutinesDispatchersProvider
import webtrekk.android.sdk.util.trackRequest
import webtrekk.android.sdk.util.trackingParams
import webtrekk.android.sdk.util.trackingParamsMediaParam

/**
 * Created by Aleksandar Marinkovic on 16/07/2020.
 * Copyright (c) 2020 MAPP.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TrackCustomMediaTest : BaseExternalTest() {
    @MockK
    lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    lateinit var trackCustomMedia: TrackCustomMedia

    @BeforeAll
    override fun setup() {
        super.setup()
        trackCustomMedia = TrackCustomMedia(
            coroutineContext,
            cacheTrackRequestWithCustomParams
        )
    }

    @Test
    fun `if opt out is active then return and don't track`() = runTest {
        val params = TrackCustomMedia.Params(
            trackRequest = trackRequest,
            trackingParams = trackingParamsMediaParam,
            isOptOut = true
        )

        trackCustomMedia(params, coroutinesDispatchersProvider())

        coVerify {
            cacheTrackRequestWithCustomParams wasNot Called
        }
    }

    @Test
    fun `verify media request when user is optOut`() = runTest {
        val params = TrackCustomMedia.Params(
            trackRequest = trackRequest,
            trackingParams = trackingParams,
            isOptOut = false
        )
        trackCustomMedia(params, coroutinesDispatchersProvider())

        val trackingParamsWithCT =
            cacheTrackRequestWithCustomParamsParams.trackingParams.toMutableMap()

        val cacheTrackRequestWithCT = CacheTrackRequestWithCustomParams.Params(
            trackRequest, trackingParamsWithCT
        )

        coVerify {
            cacheTrackRequestWithCustomParams(cacheTrackRequestWithCT)
        }
    }
}
