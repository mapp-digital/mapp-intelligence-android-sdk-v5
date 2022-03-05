/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package webtrekk.android.sdk.util

import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.extension.toCustomParams

// Data objects
internal val trackRequest = TrackRequest(
    name = "page 1",
    forceNewSession = "1",
    appFirstOpen = "1"
).apply { id = 1 }
internal val trackingParams = mapOf(
    "cs" to "val 1",
    "cd" to "val 2"
)

internal val trackingParamsMediaParam = mapOf(
    MediaParam.MEDIA_POSITION to "232",
    MediaParam.MEDIA_DURATION to "543",
    MediaParam.MEDIA_ACTION to "init"
)

internal val trackingPadrams = mapOf(
    "cs" to "val 1",
    "cd" to "val 2"
)
internal val customParams = trackingParams.toCustomParams(trackRequestId = trackRequest.id)
internal val dataTrack = DataTrack(
    trackRequest = trackRequest,
    customParams = customParams
)
internal val trackRequests = listOf(
    TrackRequest(name = "page 1", forceNewSession = "1", appFirstOpen = "1").apply { this.id = 1 },
    TrackRequest(name = "page 2", forceNewSession = "0", appFirstOpen = "0").apply { this.id = 2 },
    TrackRequest(name = "page 3", forceNewSession = "0", appFirstOpen = "0").apply { this.id = 3 },
    TrackRequest(name = "page 4", forceNewSession = "0", appFirstOpen = "0").apply { this.id = 4 }
)
internal val dataTracks = listOf(
    DataTrack(
        trackRequest = TrackRequest(
            name = "page 1",
            forceNewSession = "1",
            appFirstOpen = "1",
            requestState = TrackRequest.RequestState.NEW
        ).apply { id = 1 },
        customParams = listOf(
            CustomParam(
                trackId = 1,
                paramKey = "cs",
                paramValue = "val 1"
            ),
            CustomParam(
                trackId = 1,
                paramKey = "cd",
                paramValue = "val 2"
            )
        )
    ),
    DataTrack(
        trackRequest = TrackRequest(
            name = "page 2",
            forceNewSession = "0",
            appFirstOpen = "0",
            requestState = TrackRequest.RequestState.NEW
        ).apply {
            this.id = 2
        },
        customParams = listOf(
            CustomParam(
                trackId = 2,
                paramKey = "cs",
                paramValue = "val 3"
            )
        )
    ),
    DataTrack(
        trackRequest = TrackRequest(
            name = "page 3",
            forceNewSession = "0",
            appFirstOpen = "0",
            requestState = TrackRequest.RequestState.FAILED
        ).apply {
            this.id = 3
        },
        customParams = emptyList()
    ),
    DataTrack(
        trackRequest = TrackRequest(
            name = "page 4",
            forceNewSession = "0",
            appFirstOpen = "0",
            requestState = TrackRequest.RequestState.DONE
        ).apply {
            this.id = 4
        },
        customParams = emptyList()
    )
)

// Internal interactors params
internal val cacheTrackRequestParams = CacheTrackRequest.Params(trackRequest)
internal val cacheTrackRequestWithCustomParamsParams = CacheTrackRequestWithCustomParams.Params(
    trackRequest = trackRequest,
    trackingParams = trackingParams
)

internal val configuration = WebtrekkConfiguration.Builder(listOf("11111111"), "www.google.com")
    .build()
