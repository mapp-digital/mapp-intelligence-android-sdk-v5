package webtrekk.android.sdk.domain.external

import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
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
internal class TrackCustomMediaTest : BaseExternalTest() {
    @MockK
    lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    lateinit var trackCustomMedia: TrackCustomMedia

    @Before
    override fun setup() {
        super.setup()
        MockKAnnotations.init(this, relaxUnitFun = true)
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
        assertThat(params.isOptOut).isFalse()
    }
}
