package webtrekk.android.sdk.domain.external

import io.mockk.Called
import io.mockk.coVerify
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import webtrekk.android.sdk.api.RequestType
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
internal class TrackCustomMediaTest : AbstractExternalInteractor() {
    val cacheTrackRequestWithCustomParams = mockkClass(CacheTrackRequestWithCustomParams::class)
    val trackCustomMedia = TrackCustomMedia(
        coroutineContext,
        cacheTrackRequestWithCustomParams
    )

    init {
        feature("track custom media") {

            scenario("if opt out is active then return and don't track") {
                val params = TrackCustomMedia.Params(
                    trackRequest = trackRequest,
                    trackingParams = trackingParamsMediaParam,
                    isOptOut = true
                )

                runBlocking {
                    trackCustomMedia(params, coroutinesDispatchersProvider())

                    coVerify {
                        cacheTrackRequestWithCustomParams wasNot Called
                    }
                }
            }

            scenario("verify media request when user is optOut") {
                val params = TrackCustomMedia.Params(
                    trackRequest = trackRequest,
                    trackingParams = trackingParams,
                    isOptOut = false
                )

                runBlocking {
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
        }
    }
}